package com.gaziyev.microinstaclone.mediaservice.exception;

import java.io.IOException;

public class StorageException extends RuntimeException {
	public StorageException(String s, IOException ex) {
		super(s, ex);
	}
}
