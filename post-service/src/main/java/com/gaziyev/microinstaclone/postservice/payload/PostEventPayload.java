package com.gaziyev.microinstaclone.postservice.payload;

import com.gaziyev.microinstaclone.postservice.messaging.PostEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class PostEventPayload {

	private String id;
	private String username;
	private String imageUrl;
	private String caption;
	private PostEventType eventType;
	private String lastModifiedBy;
	private Instant createdAt;
	private Instant updatedAt;
}
