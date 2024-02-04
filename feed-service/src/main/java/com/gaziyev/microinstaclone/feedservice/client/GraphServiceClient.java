package com.gaziyev.microinstaclone.feedservice.client;

import com.gaziyev.microinstaclone.feedservice.dto.User;
import com.gaziyev.microinstaclone.feedservice.payload.PagedResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "graph-service")
public interface GraphServiceClient {

    String GET_FIND_USERS_FOLLOWERS = "/users/paginated/{username}/followers";

    @GetMapping(GET_FIND_USERS_FOLLOWERS)
    ResponseEntity<PagedResult<User>> findFollowers(
        @RequestHeader("Authorization") String token,
        @PathVariable String username,
        @RequestParam int page,
        @RequestParam int size
    );
}
