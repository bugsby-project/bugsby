package com.bugsby.datalayer.service.exceptions;

public class GitHubProjectDetailsException extends RuntimeException {
    public GitHubProjectDetailsException(String message) {
        super(message);
    }
}
