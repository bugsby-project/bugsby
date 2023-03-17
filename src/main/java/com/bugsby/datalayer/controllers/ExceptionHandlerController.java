package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.controllers.security.SecurityConstants;
import com.bugsby.datalayer.service.exceptions.AiServiceException;
import com.bugsby.datalayer.service.exceptions.EmailTakenException;
import com.bugsby.datalayer.service.exceptions.GitHubProjectDetailsException;
import com.bugsby.datalayer.service.exceptions.IssueNotFoundException;
import com.bugsby.datalayer.service.exceptions.ProjectNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserAlreadyInProjectException;
import com.bugsby.datalayer.service.exceptions.UserNotFoundException;
import com.bugsby.datalayer.service.exceptions.UserNotInProjectException;
import com.bugsby.datalayer.swagger.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.net.ConnectException;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = ConnectException.class)
    public ResponseEntity<ErrorResponse> handleConnectException(ConnectException e) {
        return new ResponseEntity<>(new ErrorResponse().message("Connection refused"), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(value = AiServiceException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceException(AiServiceException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.BAD_REQUEST);
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

    @ExceptionHandler(value = EmailTakenException.class)
    public ResponseEntity<ErrorResponse> handleEmailTakenException(EmailTakenException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = IssueNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIssueNotFoundException(IssueNotFoundException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e) {
        return new ResponseEntity<>(new ErrorResponse().message(SecurityConstants.AUTHENTICATION_FAILED_MESSAGE), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = GitHubProjectDetailsException.class)
    public ResponseEntity<ErrorResponse> handleGitHubProjectDetailsException(GitHubProjectDetailsException e) {
        return new ResponseEntity<>(new ErrorResponse().message(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
