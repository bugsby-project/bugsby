package com.bugsby.datalayer.service.exceptions;

public class UserAlreadyInProjectException extends RuntimeException {
    public UserAlreadyInProjectException(String message) {
        super(message);
    }
}
