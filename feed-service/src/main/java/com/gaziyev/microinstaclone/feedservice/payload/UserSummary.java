package com.gaziyev.microinstaclone.feedservice.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserSummary {

    private String id;
    private String username;
    private String name;
    private String profilePic;
}
