package com.bugsby.datalayer.exceptions;

public class EmailTakenException extends Exception {
    public EmailTakenException(String message) {
        super(message);
    }
}
