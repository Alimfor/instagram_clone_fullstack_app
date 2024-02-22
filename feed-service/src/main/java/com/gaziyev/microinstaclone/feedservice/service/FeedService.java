package com.gaziyev.microinstaclone.feedservice.service;

import com.gaziyev.microinstaclone.feedservice.payload.Post;
import com.gaziyev.microinstaclone.feedservice.entity.UserFeed;
import com.gaziyev.microinstaclone.feedservice.exception.ResourceNotFoundException;
import com.gaziyev.microinstaclone.feedservice.dto.SliceResultDTO;
import com.gaziyev.microinstaclone.feedservice.util.AppConstants;
import com.gaziyev.microinstaclone.feedservice.util.InstantDateTypeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class FeedService {

    private final AuthService authService;
    private final PostService postService;
    private final RedisTemplate<String, String> redisTemplate;

    public SliceResultDTO<Post> getUserFeed(String username, Optional<String> pagingState) {

        log.info("Fetching feed for user {}. Is first page? {}", username, pagingState.isEmpty());

        String feedKey = "user_feed:" + username;

        int startIndex = 0;
        int pageSize = AppConstants.PAGE_SIZE;
        if (pagingState.isPresent()) {
            startIndex = Integer.parseInt(pagingState.get());
        }

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantDateTypeAdapter())
                .create();

        List<String> jsonUserFeeds =
                redisTemplate.opsForList().range(feedKey, startIndex, startIndex + pageSize - 1);

        List<UserFeed> userFeeds = Objects.requireNonNull(jsonUserFeeds)
                .stream()
                .map(json -> gson.fromJson(json, UserFeed.class))
                .toList();

        if (userFeeds.isEmpty()) {
            throw new ResourceNotFoundException(String.format("Feed not found for user %s", username));
        }

        List<Post> posts = getPosts(userFeeds);
        String pageState = null;
        int accessedPages = startIndex + pageSize;

        if (userFeeds.size() > accessedPages) {
            pageState = String.valueOf(startIndex + pageSize);
        }

        return SliceResultDTO
                .<Post>builder()
                .content(posts)
                .isLastPage(pageState == null)
                .pagingState(pageState)
                .build();
    }

    private List<Post> getPosts(List<UserFeed> userFeeds) {

        String token = authService.getAccessToken();

        List<String> postIds = userFeeds.stream()
                .map(UserFeed::getPostId)
                .toList();

        List<Post> posts = postService.findPostsIn(token, postIds);

        List<String> usernames = posts.stream()
                .map(Post::getUsername)
                .distinct()
                .toList();

        Map<String, String> usersProfilePics =
                authService.getUsersNameWithProfilePic(token, new ArrayList<>(usernames));

        posts.forEach(post -> post.setUserProfilePic(
                usersProfilePics.get(post.getUsername())
        ));

        return posts;
    }
}
