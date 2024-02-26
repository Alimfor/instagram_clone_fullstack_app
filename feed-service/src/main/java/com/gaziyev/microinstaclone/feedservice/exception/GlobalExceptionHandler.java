package com.gaziyev.microinstaclone.feedservice.exception;

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

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnableToGetAccessTokenException.class)
    public ResponseEntity<?> handleUnableToGetAccessTokenException(UnableToGetAccessTokenException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UnableToGetFollowersException.class)
    public ResponseEntity<?> handleUnableToGetFollowersException(UnableToGetFollowersException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnableToGetPostsException.class)
    public ResponseEntity<?> handleUnableToGetPostsException(UnableToGetPostsException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnableToGetUsersException.class)
    public ResponseEntity<?> handleUnableToGetUsersException(UnableToGetUsersException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Map<String,String>> handleCustomException(String message, HttpStatus status) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", message);
        return new ResponseEntity<>(errorMap, new HttpHeaders(), status);
    }
}
