package com.gaziyev.microinstaclone.authservice.dto;

import com.gaziyev.microinstaclone.authservice.messaging.UserEventType;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserEventPayloadDTO {

    private String id;
    private String username;
    private String email;
    private String displayName;
    private String profilePictureUrl;
    private String oldProfilePictureUrl;
    private UserEventType eventType;
}
