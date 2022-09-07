package com.bugsby.datalayer.service;

import com.bugsby.datalayer.controllers.utils.Constants;
import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.ProfanityLevel;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.SeverityLevel;
import com.bugsby.datalayer.model.Status;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.repository.InvolvementRepository;
import com.bugsby.datalayer.repository.IssueRepository;
import com.bugsby.datalayer.repository.ProjectRepository;
import com.bugsby.datalayer.repository.UserRepository;
import com.bugsby.datalayer.service.ai.Predictor;
import com.bugsby.datalayer.service.exceptions.AiServiceException;
import com.bugsby.datalayer.service.exceptions.EmailTakenException;
import com.bugsby.datalayer.service.exceptions.IssueNotFoundException;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.service.exceptions.UsernameTakenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@org.springframework.stereotype.Service
public class MasterService implements Service {
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final InvolvementRepository involvementRepository;
    private final IssueRepository issueRepository;
    private final Predictor predictor;

    public MasterService(@Autowired UserRepository userRepository,
                         @Autowired ProjectRepository projectRepository,
                         @Autowired InvolvementRepository involvementRepository,
                         @Autowired IssueRepository issueRepository,
                         @Autowired Predictor predictor) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.involvementRepository = involvementRepository;
        this.issueRepository = issueRepository;
        this.predictor = predictor;
    }

    @Override
    @Transactional
    public User createAccount(User user) throws UsernameTakenException, EmailTakenException {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(user1 -> {
                    throw new UsernameTakenException(Constants.USERNAME_TAKEN_ERROR_MESSAGE);
                });

        userRepository.findByEmail(user.getEmail())
                .ifPresent(user1 -> {
                    throw new EmailTakenException(Constants.EMAIL_TAKEN_ERROR_MESSAGE);
                });

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User login(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(Constants.USER_NOT_FOUND_ERROR_MESSAGE));
    }

    @Override
    @Transactional
    public Project createProject(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        project.getInvolvements()
                .forEach(involvement -> involvement.setProject(project));
        return projectRepository.save(project);
    }

    @Override
    @Transactional
    public Project getProjectById(long id) {
        return projectRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Set<Involvement> getInvolvementsByUsername(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .map(User::getInvolvements)
                .orElseThrow(() -> new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE));
    }

    @Override
    @Transactional
    public Involvement addParticipant(Involvement involvement, User requester) throws UserNotInProjectException, UserNotFoundException, ProjectNotFoundException, UserAlreadyInProjectException {
        userRepository.findById(requester.getId())
                .map(User::getInvolvements)
                .map(involvements -> involvements.stream()
                        .anyMatch(involvement1 -> involvement1.getProject().getId().equals(involvement.getProject().getId())))
                // verify if the requester is part of the project
                .map(aBoolean -> Optional.of(aBoolean)
                        .filter(Boolean::booleanValue)
                        .orElseThrow(() -> new UserNotInProjectException("Requester is not in the project")))
                .orElseThrow(() -> new UserNotFoundException("Requester does not exist"));

        Optional<User> userParticipant = userRepository.findByUsername(involvement.getUser().getUsername());
        userParticipant.orElseThrow(() -> new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE));

        Optional<Project> projectOptional = projectRepository.findById(involvement.getProject().getId());
        projectOptional.orElseThrow(() -> new ProjectNotFoundException("Project does not exist"));

        // verify if the user is already a participant
        boolean isParticipant = userParticipant.get()
                .getInvolvements()
                .stream()
                .anyMatch(involvement1 -> involvement1.getProject().equals(projectOptional.get()));
        Optional.of(isParticipant)
                .filter(aBoolean -> !aBoolean)
                .orElseThrow(() -> new UserAlreadyInProjectException("The user is already a participant"));

        involvement.setUser(userParticipant.get());
        involvement.setProject(projectOptional.get());

        return involvementRepository.save(involvement);
    }

    @Override
    @Transactional
    public List<String> getAllUsernames() {
        return userRepository.getUsernames();
    }

    @Override
    @Transactional
    public Issue addIssue(Issue issue) throws UserNotInProjectException, UserNotFoundException, AiServiceException {
        checkOffensiveLanguage(issue);

        // check if the reporter is a valid user
        userRepository.findById(issue.getReporter().getId())
                .orElseThrow(() -> new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE));

        // verify the assignee
        Optional.ofNullable(issue.getAssignee())
                .map(User::getId)
                .map(id -> userRepository.findById(id)
                        .orElseThrow(() -> new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE)))
                .map(User::getInvolvements)
                .map(involvements -> Optional.of(involvements.stream()
                                .anyMatch(involvement -> involvement.getProject().getId().equals(issue.getProject().getId())))
                        .filter(Boolean::booleanValue)
                        .orElseThrow(() -> new UserNotInProjectException("The assignee is not a participant")));

        return issueRepository.save(issue);
    }

    @Override
    @Transactional
    public List<Issue> getAssignedIssues(String username) throws UserNotFoundException {
        return userRepository.findByUsername(username)
                .map(User::getAssignedIssues)
                .map(issues -> issues.stream()
                        .sorted(Comparator.comparing(Issue::getStatus))
                        .toList())
                .orElseThrow(() -> new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE));
    }

    @Override
    @Transactional
    public Issue getIssueById(long id) {
        return issueRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Issue deleteIssue(long id, String requesterUsername) throws IssueNotFoundException, UserNotInProjectException {
        Optional<Issue> issue = issueRepository.findById(id);
        issue.orElseThrow(() -> new IssueNotFoundException(Constants.ISSUE_DOES_NOT_EXIST_ERROR_MESSAGE));

        if (!isParticipantInProject(issue.get(), requesterUsername)) {
            throw new UserNotInProjectException("The requester is not part of the project");
        }

        // removing detached entities
        issue.get().getProject()
                .getIssues()
                .removeIf(issue1 -> issue1.equals(issue.get()));
        issue.get().getAssignee()
                .getAssignedIssues()
                .removeIf(issue1 -> issue1.equals(issue.get()));
        issueRepository.deleteById(id);
        return issue.get();
    }

    @Override
    @Transactional
    public Issue updateIssue(Issue issue, String requesterUsername) throws IllegalArgumentException, UserNotInProjectException, IssueNotFoundException {
        Optional.ofNullable(issue)
                .orElseThrow(IllegalArgumentException::new);
        Optional.ofNullable(requesterUsername)
                .orElseThrow(IllegalArgumentException::new);

        return issueRepository.findById(issue.getId())
                // check if the requester is part of the project
                .map(issue1 -> Optional.of(isParticipantInProject(issue1, requesterUsername))
                        .filter(Boolean::booleanValue)
                        .map(aBoolean -> issue)
                        .orElseThrow(() -> new UserNotInProjectException("The requester is not part of the project")))
                .map(issueRepository::save)
                .orElseThrow(() -> new IssueNotFoundException(Constants.ISSUE_DOES_NOT_EXIST_ERROR_MESSAGE));
    }

    @Override
    @Transactional
    public SeverityLevel predictSeverityLevel(String title) throws AiServiceException {
        return predictor.predictSeverityLevel(title);
    }

    @Override
    @Transactional
    public IssueType predictIssueType(String title) throws AiServiceException {
        return predictor.predictIssueType(title);
    }

    @Override
    @Transactional
    public List<Issue> retrieveDuplicateIssues(Issue issue) throws ProjectNotFoundException, AiServiceException {
        List<Issue> projectIssues = projectRepository.findById(issue.getProject().getId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with id " + issue.getProject().getId() + " does not exist"))
                .getIssues()
                .stream()
                .toList();
        return predictor.detectDuplicateIssues(projectIssues, issue);
    }

    private boolean isParticipantInProject(Issue issue, String username) {
        return issue.getProject()
                .getInvolvements()
                .stream()
                .anyMatch(involvement -> involvement.getUser().getUsername().equals(username));
    }

    private void checkOffensiveLanguage(Issue issue) throws IllegalArgumentException, AiServiceException {
        if (predictor.predictProfanityLevel(issue.getTitle()).equals(ProfanityLevel.OFFENSIVE)) {
            throw new IllegalArgumentException("Title contains offensive language");
        }

        if (predictor.predictProfanityLevel(issue.getDescription()).equals(ProfanityLevel.OFFENSIVE)) {
            throw new IllegalArgumentException("Description contains offensive language");
        }
    }
}
