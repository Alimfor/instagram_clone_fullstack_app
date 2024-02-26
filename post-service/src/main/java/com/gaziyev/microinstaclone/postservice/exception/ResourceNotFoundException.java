package com.gaziyev.microinstaclone.postservice.exception;

public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String resource) {
		super(
				String.format("Resource %s not found", resource)
		);
	}
}
