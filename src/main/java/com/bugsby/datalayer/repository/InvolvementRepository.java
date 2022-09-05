package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.Involvement;
import com.bugsby.datalayer.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface InvolvementRepository extends PagingAndSortingRepository<Involvement, Long> {

    /**
     * Method for obtaining the involvements of a certain user
     *
     * @param user, the user to retrieve their involvements in projects
     * @return an {@code Iterable} of involvements
     */
    List<Involvement> findByUser(User user);
}
