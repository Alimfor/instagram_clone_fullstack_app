package com.gaziyev.microinstaclone.feedservice.payload;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class Post {

    private String id;
    private String username;
    private String userProfilePic;
    private String imageUrl;
    private String lastModifiedBy;
    private Instant createdAt;
    private Instant updatedAt;
}
