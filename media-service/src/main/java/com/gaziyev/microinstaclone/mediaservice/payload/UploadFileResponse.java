package com.gaziyev.microinstaclone.mediaservice.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadFileResponse {

	private String filename;
	private String uri;
	private String fileType;
}
