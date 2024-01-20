package com.gaziyev.microinstaclone.authservice.payload;

import com.gaziyev.microinstaclone.authservice.messaging.UserEventType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventPayload {

	private String id;
	private String username;
	private String email;
	private String displayName;
	private String profilePictureUrl;
	private String oldProfilePictureUrl;
	private UserEventType eventType;
}
