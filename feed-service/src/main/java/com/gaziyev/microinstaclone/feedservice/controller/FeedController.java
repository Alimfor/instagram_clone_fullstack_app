package com.gaziyev.microinstaclone.feedservice.controller;

import com.gaziyev.microinstaclone.feedservice.dto.Post;
import com.gaziyev.microinstaclone.feedservice.payload.SliceResult;
import com.gaziyev.microinstaclone.feedservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;
    private static final String GET_FEED_BY_USERNAME = "/feed/{username}";

    @GetMapping(GET_FEED_BY_USERNAME)
    public ResponseEntity<SliceResult<Post>> getFeedByUsername(
            @PathVariable String username,
            @RequestParam(value = "ps", required = false) Optional<String> pagingState
    ) {

        log.info("fetching feed for user {}. Is first page? {}",
                username, pagingState.isEmpty()
        );

        return ResponseEntity.ok(feedService.getUserFeed(username, pagingState));
    }
}
