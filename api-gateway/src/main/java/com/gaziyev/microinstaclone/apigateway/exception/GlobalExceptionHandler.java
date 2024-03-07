package com.gaziyev.microinstaclone.apigateway.exception;

import org.bouncycastle.jce.provider.AnnotatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.resource.NoResourceFoundException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> handleNoResourceFoundException(NoResourceFoundException e) {

        final String errorMessage = "specify path is not found";
        return handleCustomRuntimeException(errorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AnnotatedException.class)
    public ResponseEntity<?> handleAnnotatedException(AnnotatedException e) {
        return handleCustomRuntimeException(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, String>> handleCustomRuntimeException(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(Map.of("message", message));

    }
}
