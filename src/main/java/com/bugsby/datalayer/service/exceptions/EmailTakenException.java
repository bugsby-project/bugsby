package com.bugsby.datalayer.service.exceptions;

public class EmailTakenException extends Exception {
    public EmailTakenException(String message) {
        super(message);
    }
}
