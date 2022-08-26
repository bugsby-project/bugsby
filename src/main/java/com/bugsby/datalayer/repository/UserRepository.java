package com.bugsby.datalayer.repository;

import com.bugsby.datalayer.model.User;

import java.util.Optional;

public interface UserRepository extends Repository<Long, User> {

    /**
     * Method for obtaining the User with a specific username
     *
     * @param username: String, the desired username
     * @return an {@code Optional} with the user with the given username, null if it does not exist
     * @throws IllegalArgumentException if username is null
     */
    Optional<User> findUserByUsername(String username);

    /**
     * Method for obtaining the User with a specific email
     *
     * @param email: String, the desired email
     * @return an {@code Optional} with the user with the given email, null if it does not exist
     * @throws IllegalArgumentException if email is null
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Method for obtaining all the usernames of the saved users
     *
     * @return a collection with all the usernames
     */
    Iterable<String> getAllUsernames();
}
