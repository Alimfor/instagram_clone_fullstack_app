package com.gaziyev.microinstaclone.apigateway.exception;

import org.bouncycastle.jce.provider.AnnotatedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {

        String pattern = "\\b(\\w+)-service\\b";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(e.getMessage());

        if (m.find()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("message", "Unable to find instance for " + m.group()));
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }

    @ExceptionHandler(AnnotatedException.class)
    public ResponseEntity<Map<String, String>> handleAnnotatedException(AnnotatedException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", e.getMessage()));
    }
}
