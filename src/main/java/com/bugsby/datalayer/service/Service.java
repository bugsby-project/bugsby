package com.bugsby.datalayer.service;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.IssueType;
import com.bugsby.datalayer.model.PrefilledIssue;
import com.bugsby.datalayer.model.PrefilledIssueExpectedBehaviourCount;
import com.bugsby.datalayer.model.Project;
import com.bugsby.datalayer.model.SeverityLevel;
import com.bugsby.datalayer.model.User;
import com.bugsby.datalayer.service.exceptions.EmailTakenException;
import com.bugsby.datalayer.service.exceptions.IssueNotFoundException;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.service.exceptions.UsernameTakenException;

import java.util.List;
import java.util.Set;

public interface Service {

    /**
     * Method for creating a new account
     *
     * @param user: User, encapsulation of all the details of the user
     * @return the user with the assigned id
     * @throws UsernameTakenException, if the username is already used
     * @throws EmailTakenException,    if the email is already used
     */
    User createAccount(User user) throws UsernameTakenException, EmailTakenException;

    /**
     * Method for finding a user based on the username
     *
     * @param username: String, the desired username
     * @return the user, if there is a user with the specified username and password
     * @throws UserNotFoundException, if there is no user
     */
    User login(String username) throws UserNotFoundException;

    /**
     * Method for finding a user based on their unique identifier
     *
     * @param id, the id of the desired user
     * @return the user with the given id
     * @throws UserNotFoundException, if there is no user with the given id
     */
    User findUser(long id) throws UserNotFoundException;

    /**
     * Method for creating a new project
     *
     * @param project, the project to add
     * @return - the project with an assigned identifier, if the operation is successful
     * - null, otherwise
     */
    Project createProject(Project project);

    /**
     * Method for updating a project
     *
     * @param projectId, the ID of the project to update
     * @param updateProjectRequest, the project details to update
     * @return the project with the updated details
     */
    Project updateProject(Long projectId, com.bugsby.datalayer.swagger.model.UpdateProjectRequest updateProjectRequest);

    /**
     * Method for retrieving the information of a project, based on its id
     *
     * @param id, the id of the desired project
     * @return - the project, if there is a project with the given id
     * - null, otherwise
     */
    Project getProjectById(long id);

    /**
     * Method for retrieving all the involvements of a certain user, based on their username
     *
     * @param username, the username of the user to retrieve the involvements
     * @return a {@code Set} containing the involvements of the user with the specified username.
     * Returns an empty set if there are no involvements to retrieve.
     * @throws UserNotFoundException if there is no user with the specified username
     */
    Set<Involvement> getInvolvementsByUsername(String username) throws UserNotFoundException;

    /**
     * Method for adding a participant to the project
     *
     * @param involvement, encapsulation of the project and the user, along with their role in the project.
     *                     It is expected that the {@code User} of involvement has a valid username set and the {@code Project}, a valid id.
     * @param requester,   the user that initiated the process. It is expected that the requester has a valid identifier set.
     * @return - the involvement with an identifier assigned, if the operation is successful
     * - null, otherwise
     * @throws UserNotInProjectException     if the requester is not a participant to the project
     * @throws UserNotFoundException         if either the desired participant or the requester do not exist
     * @throws ProjectNotFoundException      if the project of the involvement does not exist
     * @throws UserAlreadyInProjectException if the user is already a participant in the project
     */
    Involvement addParticipant(Involvement involvement, User requester) throws UserNotInProjectException, UserNotFoundException, ProjectNotFoundException, UserAlreadyInProjectException;

    /**
     * Method for obtaining all the usernames of the saved users
     *
     * @return a list containing all the usernames
     */
    List<String> getAllUsernames();

    /**
     * Method for adding an issue
     *
     * @param issue, the issue to be added. It is expected that its reporter and project have at least a valid identifier among their fields
     * @return - the issue with an identifier assigned, if the operation is successful (e.g. the issue does not contain offensive language in its title and description)
     * - null, otherwise
     * @throws UserNotInProjectException if the reporter or assignee of the issue is not a participant to the issue's project
     * @throws UserNotFoundException     if the reporter or assignee of the issue does not exist
     */
    Issue addIssue(Issue issue) throws UserNotInProjectException, UserNotFoundException;

    /**
     * Method for retrieving the assigned issues of a particular user, identified by their username, ordered by status in descending order
     *
     * @param username, the username of the {@code User} to retrieve the assigned issues from
     * @return a list containing the {@param username}'s assigned issues
     * @throws UserNotFoundException if there is no user with the username set to {@param username}
     */
    List<Issue> getAssignedIssues(String username) throws UserNotFoundException;

    /**
     * Method for retrieving the information of an issue, identified by its ID
     *
     * @param id, the identifier of the desired issue
     * @return - the issue, if an issue with the ID {@param id} exists
     * - null, otherwise
     */
    Issue getIssueById(long id);

    /**
     * Method for deleting an issue, based on its identifier
     *
     * @param id,                the identifier of the issue wished to be deleted
     * @param requesterUsername, the username of the {@code User} that requests the deletion of the issue
     * @return the issue, if an issue with the ID {@param id} exists and {@param requesterUsername} has the necessary rights
     * @throws IssueNotFoundException,    if there is no issue with the ID {@param id}
     * @throws UserNotInProjectException, if the {@param requesterUsername} is not part of the issue's project (hence, does not have the right to delete the issue)
     */
    Issue deleteIssue(long id, String requesterUsername) throws IssueNotFoundException, UserNotInProjectException;

    /**
     * Method for updating an issue
     *
     * @param issue,             the issue to update its fields. Should have a valid identifier assigned
     * @param requesterUsername, the username of the {@code User} that requests the change. The user should be part of the {@param issue}'s project
     * @return the issue, if {@param issue} is updated successfully, null, otherwise
     * @throws IllegalArgumentException,  if {@param issue} or {@param requesterUsername} is null
     * @throws UserNotInProjectException, if {@param requesterUsername} is not part of {@param issue}'s project
     * @throws IssueNotFoundException,    if {@param issue}'s identifier is not valid
     */
    Issue updateIssue(Issue issue, String requesterUsername) throws IllegalArgumentException, UserNotInProjectException, IssueNotFoundException;

    /**
     * Method for predicting the severity level of an issue, based on its title
     *
     * @param title, the title of the issue to predict the severity level to
     * @return the predicted severity level
     */
    SeverityLevel predictSeverityLevel(String title);

    /**
     * Method for predicting the label (type) of an issue, based on its title
     *
     * @param title, the title of the issue to predict the type to
     * @return the predicted issue type
     */
    IssueType predictIssueType(String title);

    /**
     * Method for retrieving the possible duplicate issues, in regard to another issue
     *
     * @param issue, the issue to retrieve the possible duplicates of
     * @return a {@code List} containing the possible duplicate issues of {@code issue}
     * @throws ProjectNotFoundException if the {@code issue}'s project id does not match a real project
     */
    List<Issue> retrieveDuplicateIssues(Issue issue) throws ProjectNotFoundException;

    /**
     * Method for retrieving the information of a prefilled issue, identified by its ID
     *
     * @param id, the identifier of the desired prefilled issue
     * @return - the prefilled issue, if a prefilled issue with the ID {@param id} exists
     * - null, otherwise
     */
    PrefilledIssue getPrefilledIssueById(long id);

    /**
     * Method for retrieving the number of prefilled issues, grouped by their expected behaviour for a particular project
     * @param projectId, the ID of the project to retrieve the statistics
     * @return the number of prefilled issues, grouped by their expected behaviour
     */
    List<PrefilledIssueExpectedBehaviourCount> getPrefilledIssuesCountByExpectedBehaviourWithProject(Long projectId);
}
