package com.bugsby.datalayer.service.utils;

public class Constants {

    /**
     * Error message displayed when creating a new account, if the respective username is already in use
     */
    public static final String USERNAME_TAKEN_ERROR_MESSAGE = "This username is already registered";

    /**
     * Error message displayed when creating a new account, if the respective email is already in use
     */
    public static final String EMAIL_TAKEN_ERROR_MESSAGE = "This email is already used";

    /**
     * Error message displayed when logging into the application fails due to wrong credentials
     */
    public static final String USER_NOT_FOUND_ERROR_MESSAGE = "User not found";

    /**
     * Error message displayed when trying to access information of a user that does not exist
     */
    public static final String USER_DOES_NOT_EXIST_ERROR_MESSAGE = "User does not exist";

    /**
     * Error message displayed when trying to access an issue that does not exist
     */
    public static final String ISSUE_DOES_NOT_EXIST_ERROR_MESSAGE = "Issue does not exist";
}
