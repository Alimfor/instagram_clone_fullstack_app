package com.gaziyev.microinstaclone.postservice.exception;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return handleCustomException(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NotAllowedException.class)
    public ResponseEntity<?> handleNotAllowedException(NotAllowedException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<Map<String,String>> handleCustomException(String message, HttpStatus status) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", message);
        return new ResponseEntity<>(errorMap, new HttpHeaders(), status);
    }
}
