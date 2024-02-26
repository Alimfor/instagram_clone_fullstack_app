package com.gaziyev.microinstaclone.mediaservice.exception;


public class InvalidFileException extends RuntimeException {
	public InvalidFileException(String filedToStoreEmptyFile) {
		super(filedToStoreEmptyFile);
	}

	public InvalidFileException(String message, Throwable cause) {
		super(message, cause);
	}
}
