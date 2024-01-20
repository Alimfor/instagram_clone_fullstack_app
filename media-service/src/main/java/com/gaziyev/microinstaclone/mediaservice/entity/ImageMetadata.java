package com.gaziyev.microinstaclone.mediaservice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
@Document
public class ImageMetadata {

	@Id
	private String id;

	@CreatedBy
	private String username;

	@NonNull
	private String filename;

	@NonNull
	private String uri;

	@NonNull
	private String fileType;

	@CreatedDate
	private Instant createdAt;
}
