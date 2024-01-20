package com.gaziyev.microinstaclone.mediaservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class StorageException extends RuntimeException {
	public StorageException(String s, IOException ex) {
		super(s, ex);
	}
}
