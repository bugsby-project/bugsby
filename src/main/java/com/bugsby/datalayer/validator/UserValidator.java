package com.bugsby.datalayer.validator;

import com.bugsby.datalayer.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserValidator implements Validator<Long, User> {
    @Override
    public void validate(User entity) {
        String errors = "";
        if (!validateUsername(entity))
            errors += ErrorMessages.USER_INVALID_USERNAME;
        if (!validatePassword(entity))
            errors += ErrorMessages.USER_INVALID_PASSWORD;
        if (!validateFirstName(entity))
            errors += ErrorMessages.USER_INVALID_FIRST_NAME;
        if (!validateLastName(entity))
            errors += ErrorMessages.USER_INVALID_LAST_NAME;
        if (!validateEmail(entity))
            errors += ErrorMessages.USER_INVALID_EMAIL;
        if (!errors.equals(""))
            throw new ValidationException(errors);
    }

    /**
     * Method for verifying if a user's username is not empty
     *
     * @param user: User, the user to verify the username
     * @return true, if the user's username is not empty
     * false, otherwise
     */
    private boolean validateUsername(User user) {
        return user.getUsername() != null && !user.getUsername().equals("");
    }

    /**
     * Method for verifying if a user's password is not empty
     *
     * @param user: User, the user to verify the password
     * @return true, if the user's password is not empty
     * false, otherwise
     */
    private boolean validatePassword(User user) {
        return user.getPassword() != null && !user.getPassword().equals("");
    }

    /**
     * Method for verifying if a user's first name is not empty
     *
     * @param user: User, the user to verify the first name
     * @return true, if the user's first name is not empty
     * false, otherwise
     */
    private boolean validateFirstName(User user) {
        return user.getFirstName() != null && !user.getFirstName().equals("");
    }

    /**
     * Method for verifying if a user's last name is not empty
     *
     * @param user: User, the user to verify the last name
     * @return true, if the user's last name is not empty
     * false, otherwise
     */
    private boolean validateLastName(User user) {
        return user.getLastName() != null && !user.getLastName().equals("");
    }

    /**
     * Method for verifying if a user's email is a valid one
     *
     * @param user: User, the user to verify the email
     * @return true, if the user's email is in the right format
     * false, otherwise
     */
    private boolean validateEmail(User user) {
        return user.getEmail() != null && user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
