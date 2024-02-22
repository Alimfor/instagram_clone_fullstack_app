package com.gaziyev.microinstaclone.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UploadFileResponseDTO {

	private String filename;
	private String uri;
	private String fileType;
}
