package com.gaziyev.microinstaclone.graphservice.dto;

import com.gaziyev.microinstaclone.graphservice.payload.UserPayload;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequestDTO {

    private UserPayload follower;
    private UserPayload following;
}
