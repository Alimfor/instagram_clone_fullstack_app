package com.gaziyev.microinstaclone.postservice.entity;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@Setter
@RequiredArgsConstructor
@Document
public class Post {

	@Id
	private String id;

	@CreatedDate
	private Instant createdAt;

	@LastModifiedDate
	private Instant updatedAt;

	@CreatedBy
	private String username;

	@LastModifiedBy
	private String lastModifiedBy;

	@NonNull
	private String imageUrl;

	@NonNull
	private String caption;
}
