package com.gaziyev.microinstaclone.graphservice.payload;

import com.gaziyev.microinstaclone.graphservice.messaging.UserEventType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserEventPayload {

    private String id;
    private String username;
    private String email;
    private String displayName;
    private String profilePictureUrl;
    private String oldProfilePictureUrl;
    private UserEventType eventType;
}
