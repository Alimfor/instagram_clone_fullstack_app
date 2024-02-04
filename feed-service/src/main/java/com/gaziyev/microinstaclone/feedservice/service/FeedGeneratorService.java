package com.gaziyev.microinstaclone.feedservice.service;

import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import com.gaziyev.microinstaclone.feedservice.client.GraphServiceClient;
import com.gaziyev.microinstaclone.feedservice.configuration.JwtConfig;
import com.gaziyev.microinstaclone.feedservice.dto.Post;
import com.gaziyev.microinstaclone.feedservice.dto.User;
import com.gaziyev.microinstaclone.feedservice.entity.UserFeed;
import com.gaziyev.microinstaclone.feedservice.exception.UnableToGetFollowersException;
import com.gaziyev.microinstaclone.feedservice.payload.PagedResult;
import com.gaziyev.microinstaclone.feedservice.repository.FeedRepository;
import com.gaziyev.microinstaclone.feedservice.util.AppConstants;
import com.gaziyev.microinstaclone.feedservice.util.InstantDateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedGeneratorService {

    private final GraphServiceClient graphServiceClient;
    private final AuthService authService;
    private final JwtConfig jwtConfig;
    private final FeedRepository feedRepository;
    private final RedisTemplate<String, String> redisTemplate;

    public void addToFeed(Post post) {
        processFeed(post, "adding");
    }

    public void removeFromFeed(Post post) {
        processFeed(post, "removing");
    }

    private void processFeed(Post post, String action) {

        log.info("{} post to feed: {} for user: {}",
                action, post.getId(), post.getUsername()
        );

        boolean isLast = false;
        int page = 0;
        int size = AppConstants.PAGE_SIZE;

        while (!isLast) {

            ResponseEntity<PagedResult<User>> response =
                    graphServiceClient.findFollowers(
                            jwtConfig.getPrefix() + getAccessToken(),
                            post.getUsername(), page, size
                    );

            if (!response.getStatusCode().is2xxSuccessful()) {
                String message = String.format(
                        "unable to get followers: %s", response.getStatusCode()
                );
                log.error(message);
                throw new UnableToGetFollowersException(message);
            }

            PagedResult<User> result = Objects.requireNonNull(response.getBody());

            log.info("found {} followers for user: {}, page {}",
                    result.getContents().size(), post.getUsername(), page);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Instant.class, new InstantDateTypeAdapter())
                    .create();

            String feed_key = "user_feed:" + post.getUsername();

            result.getContents().stream()
                    .map(user -> convertTo(user, post))
                    .forEach(userFeed -> {
                                switch (action) {
                                    case "adding" -> redisTemplate.opsForList()
                                            .leftPush(feed_key, gson.toJson(userFeed));
                                    case "removing" -> redisTemplate.opsForList(); //TODO -> remove from redis
                                }
                            }
                    );


            isLast = result.isLast();
            page++;
        }
    }

    private String getAccessToken() {
        return authService.getAccessToken();
    }

    private UserFeed convertTo(User user, Post post) {
        return UserFeed.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .postId(post.getId())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
