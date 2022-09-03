package com.bugsby.datalayer.service.exceptions;

public class UsernameTakenException extends Exception {
    public UsernameTakenException(String message) {
        super(message);
    }
}
