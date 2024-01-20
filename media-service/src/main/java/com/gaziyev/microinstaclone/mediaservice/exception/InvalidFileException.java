package com.gaziyev.microinstaclone.mediaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFileException extends RuntimeException {
	public InvalidFileException(String filedToStoreEmptyFile) {
		super(filedToStoreEmptyFile);
	}

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
