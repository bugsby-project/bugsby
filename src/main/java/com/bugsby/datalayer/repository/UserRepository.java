package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    /**
     * Method for obtaining the User with a specific username
     *
     * @param username: String, the desired username
     * @return an {@code Optional} with the user with the given username, null if it does not exist
     */
    Optional<User> findByUsername(String username);

    /**
     * Method for obtaining the User with a specific email
     *
     * @param email: String, the desired email
     * @return an {@code Optional} with the user with the given email, null if it does not exist
     */
    Optional<User> findByEmail(String email);

    /**
     * Method for obtaining all the usernames of the saved users
     *
     * @return a collection with all the usernames
     */
    @Query(value = "select username from User")
    List<String> getUsernames();
}
