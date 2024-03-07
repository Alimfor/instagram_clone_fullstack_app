package com.gaziyev.microinstaclone.feedservice.exception;

import feign.FeignException;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        return handleCustomException(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoFallbackAvailableException.class)
    public ResponseEntity<?> handleNoFallbackAvailableException(NoFallbackAvailableException ex) {

        if (ex.getCause().getCause() instanceof UnknownHostException unknownHostException) {
            return handleCustomException(
                    String.format("service %s is not available", unknownHostException.getMessage()),
                    HttpStatus.SERVICE_UNAVAILABLE
            );
        }

        if (ex.getCause() instanceof FeignException feignException) {
            return handleCustomException(
                    String.format("wrong request to %s", feignException.request().url()),
                    HttpStatus.valueOf(feignException.status())
            );
        }

        if (ex.getCause() instanceof TimeoutException timeoutException) {
            Pattern pattern = Pattern.compile("'(.*?)Service");
            Matcher matcher = pattern.matcher(timeoutException.getMessage());

            if (matcher.find()) {
                String serviceName = matcher.group(1);
                return handleCustomException(
                        String.format("service %s is not available", serviceName),
                        HttpStatus.SERVICE_UNAVAILABLE
                );
            }
        }

        return handleCustomException(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleRuntimeException(NullPointerException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
        return handleCustomException(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnableToGetTokensException.class)
    public ResponseEntity<?> handleUnableToGetAccessTokenException(UnableToGetTokensException ex) {
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

    private ResponseEntity<Map<String, String>> handleCustomException(String message, HttpStatus status) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("message", message);
        return new ResponseEntity<>(errorMap, new HttpHeaders(), status);
    }
}
