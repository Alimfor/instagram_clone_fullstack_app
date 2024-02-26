package com.gaziyev.microinstaclone.mediaservice.exception;

public class InvalidFileNameException extends RuntimeException {
	public InvalidFileNameException(String message) {
		super(message);
	}

	public InvalidFileNameException(String message, Throwable cause) {
		super(message, cause);
	}
}
