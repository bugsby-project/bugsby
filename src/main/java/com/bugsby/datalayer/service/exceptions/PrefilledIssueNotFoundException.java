package com.bugsby.datalayer.service.exceptions;

public class PrefilledIssueNotFoundException extends RuntimeException {
    public PrefilledIssueNotFoundException(String message) {
        super(message);
    }
}
