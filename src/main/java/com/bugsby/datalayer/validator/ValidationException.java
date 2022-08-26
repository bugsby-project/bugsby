package com.bugsby.datalayer.validator;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
