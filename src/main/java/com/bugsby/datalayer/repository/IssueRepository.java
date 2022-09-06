package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.Issue;
import com.bugsby.datalayer.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface IssueRepository extends PagingAndSortingRepository<Issue, Long> {
    /**
     * Method for retrieving the assigned issues of a particular user, ordered by status in descending order
     *
     * @param user, the {@code User} to retrieve the assigned issues from
     * @return a {@code List} containing the {@param user}'s assigned issues
     */
    List<Issue> findByAssigneeOrderByStatusDesc(User user);
}
