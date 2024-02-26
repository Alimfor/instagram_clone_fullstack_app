package com.gaziyev.microinstaclone.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String username) {
        super(String.format("User with username %s not found", username));
    }
}
