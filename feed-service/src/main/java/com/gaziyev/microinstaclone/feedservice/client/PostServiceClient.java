package com.gaziyev.microinstaclone.feedservice.client;

import com.gaziyev.microinstaclone.feedservice.payload.Post;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("post-service")
public interface PostServiceClient {

    String POST_FIND_POSTS_BY_IDS = "/posts/in";

    @PostMapping(POST_FIND_POSTS_BY_IDS)
    ResponseEntity<List<Post>> findPostsByIdIn(
            @RequestHeader("Authorization") String token,
            @RequestBody List<String> ids
    );
}
