package com.bugsby.datalayer.controllers;

import com.bugsby.datalayer.service.exceptions.AiServiceException;
import com.bugsby.datalayer.swagger.model.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = AiServiceException.class)
    public ResponseEntity<ErrorResponse> handleAiServiceException(AiServiceException e) {
        return ResponseEntity.badRequest().body(new ErrorResponse().message(e.getMessage()));
    }
}
