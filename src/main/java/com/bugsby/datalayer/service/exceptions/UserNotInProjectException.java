package com.bugsby.datalayer.service.exceptions;

public class UserNotInProjectException extends RuntimeException {
    public UserNotInProjectException(String message) {
        super(message);
    }
}
