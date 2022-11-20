package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.service.exceptions.AiServiceException;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.swagger.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = AiServiceException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceException(AiServiceException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse().message(e.getMessage()));
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ProjectNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProjectNotFoundException(ProjectNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = UserNotInProjectException.class)
    public ResponseEntity<ErrorResponse> handleUserNotInProjectException(UserNotInProjectException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = UserAlreadyInProjectException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyInProjectException(UserAlreadyInProjectException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
