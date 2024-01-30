package com.gaziyev.microinstaclone.graphservice.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPayload {

    private String id;
    private String username;
    private String name;
    private String profilePic;
}
