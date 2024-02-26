package com.gaziyev.microinstaclone.graphservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UsernameNotExistsException extends RuntimeException {
    public UsernameNotExistsException(String message) {
        super(message);
    }
}
