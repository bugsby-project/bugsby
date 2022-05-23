package repository;

import model.Issue;
import model.User;

public interface IssueRepository extends Repository<Long, Issue> {
    /**
     * Method for retrieving the assigned issues of a particular user, ordered by status in descending order
     * @param user, the {@code User} to retrieve the assigned issues from
     * @return an {@code Iterable} containing the {@param user}'s assigned issues
     * @throws IllegalArgumentException is user is null
     */
    Iterable<Issue> getAssignedIssues(User user);
}
