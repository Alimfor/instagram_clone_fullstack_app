package com.gaziyev.microinstaclone.graphservice.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FollowRequest {

    private UserPayload follower;
    private UserPayload following;
}
