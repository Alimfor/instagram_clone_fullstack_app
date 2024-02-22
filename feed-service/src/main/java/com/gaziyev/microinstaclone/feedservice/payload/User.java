package com.gaziyev.microinstaclone.feedservice.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class User {

    private String userId;
    private String username;
}
