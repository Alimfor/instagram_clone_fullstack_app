package com.gaziyev.microinstaclone.feedservice.client;

import com.gaziyev.microinstaclone.feedservice.payload.User;
import com.gaziyev.microinstaclone.feedservice.dto.PagedResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("graph-service")
public interface GraphServiceClient {

    String GET_FIND_USERS_FOLLOWERS = "/paginated/{username}/followers";

    @GetMapping(GET_FIND_USERS_FOLLOWERS)
    ResponseEntity<PagedResultDTO<User>> findFollowers(
            @RequestHeader("Authorization") String token,
            @PathVariable String username,
            @RequestParam int page,
            @RequestParam int size
    );
}
