package com.gaziyev.microinstaclone.feedservice.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.Instant;

@RedisHash("users_feed")
@Getter
@Setter
@Builder
public class UserFeed {
    @Id
    private String userId;
    private String username;
    private String postId;
    private Instant createdAt;

}
