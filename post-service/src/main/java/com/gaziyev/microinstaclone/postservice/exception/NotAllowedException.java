package com.gaziyev.microinstaclone.postservice.exception;

public class NotAllowedException extends RuntimeException{

	public NotAllowedException(String username, String resource, String operation) {
		super(String.format("user %s is not allowed to %s %s",
		                    username, operation, resource
		      )
		);
	}
}
