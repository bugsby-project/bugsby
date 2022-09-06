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
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UsernameTakenException(Constants.USERNAME_TAKEN_ERROR_MESSAGE);
        }

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailTakenException(Constants.EMAIL_TAKEN_ERROR_MESSAGE);
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User login(String username) throws UserNotFoundException {
        Optional<User> result = userRepository.findByUsername(username);
        if (result.isEmpty()) {
            throw new UserNotFoundException(Constants.USER_NOT_FOUND_ERROR_MESSAGE);
        }
        return result.get();
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
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {  // the user does not exist
            throw new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE);
        }

        return user.get().getInvolvements();
    }

    @Override
    @Transactional
    public Involvement addParticipant(Involvement involvement, User requester) throws UserNotInProjectException, UserNotFoundException, ProjectNotFoundException, UserAlreadyInProjectException {
        // verify if the users are valid
        Optional<User> userRequester = userRepository.findById(requester.getId());
        if (userRequester.isEmpty()) {
            throw new UserNotFoundException("Requester does not exist");
        }

        boolean isParticipant = userRequester.get()
                .getInvolvements()
                .stream()
                .anyMatch(involvement1 -> involvement1.getProject().getId().equals(involvement.getProject().getId()));
        if (!isParticipant) {
            throw new UserNotInProjectException("Requester is not in the project");
        }

        Optional<User> userParticipant = userRepository.findByUsername(involvement.getUser().getUsername());
        if (userParticipant.isEmpty()) {
            throw new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE);
        }

        Optional<Project> projectOptional = projectRepository.findById(involvement.getProject().getId());
        if (projectOptional.isEmpty()) {
            throw new ProjectNotFoundException("Project does not exist");
        }

        // verify if the user is already a participant
        isParticipant = userParticipant.get()
                .getInvolvements()
                .stream()
                .anyMatch(involvement1 -> involvement1.getProject().equals(projectOptional.get()));
        if (isParticipant) {
            throw new UserAlreadyInProjectException("The user is already a participant");
        }

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
        Optional<User> reporter = userRepository.findById(issue.getReporter().getId());
        if (reporter.isEmpty()) {
            throw new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE);
        }

        if (issue.getAssignee() != null) {
            Optional<User> assignee = userRepository.findById(issue.getAssignee().getId());
            if (assignee.isEmpty()) {
                throw new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE);
            }

            boolean isParticipant = assignee.get()
                    .getInvolvements()
                    .stream()
                    .anyMatch(involvement -> involvement.getProject().getId().equals(issue.getProject().getId()));
            if (!isParticipant) {
                throw new UserNotInProjectException("The assignee is not a participant");
            }
        }

        issue.setStatus(Status.TO_DO);
        return issueRepository.save(issue);
    }

    @Override
    @Transactional
    public List<Issue> getAssignedIssues(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(Constants.USER_DOES_NOT_EXIST_ERROR_MESSAGE);
        }

        return user.get().getAssignedIssues()
                .stream()
                .sorted(Comparator.comparing(Issue::getStatus))
                .toList();
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
        if (issue.isEmpty()) {
            throw new IssueNotFoundException(Constants.ISSUE_DOES_NOT_EXIST_ERROR_MESSAGE);
        }

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
        if (issue == null || requesterUsername == null) {
            throw new IllegalArgumentException();
        }

        Optional<Issue> foundIssue = issueRepository.findById(issue.getId());
        if (foundIssue.isEmpty()) {
            throw new IssueNotFoundException(Constants.ISSUE_DOES_NOT_EXIST_ERROR_MESSAGE);
        }

        if (!isParticipantInProject(foundIssue.get(), requesterUsername)) {
            throw new UserNotInProjectException("The requester is not part of the project");
        }

        return issueRepository.save(issue);
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
