package com.gaziyev.microinstaclone.graphservice.controller;

import com.gaziyev.microinstaclone.graphservice.entity.User;
import com.gaziyev.microinstaclone.graphservice.payload.ApiResponse;
import com.gaziyev.microinstaclone.graphservice.payload.FollowRequest;
import com.gaziyev.microinstaclone.graphservice.service.UserService;
import com.gaziyev.microinstaclone.graphservice.util.AppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/users/followers")
    public ResponseEntity<?> follow(@RequestBody FollowRequest followRequest) {

        log.info("received a follow request follow {} following {}",
                followRequest.getFollower().getUsername(),
                followRequest.getFollowing().getUsername());

        userService.follow(
                User.builder()
                        .userId(followRequest.getFollower().getId())
                        .username(followRequest.getFollower().getUsername())
                        .name(followRequest.getFollower().getName())
                        .profilePic(followRequest.getFollower().getProfilePic())
                        .build(),

                User.builder()
                        .userId(followRequest.getFollowing().getId())
                        .username(followRequest.getFollowing().getUsername())
                        .name(followRequest.getFollowing().getName())
                        .profilePic(followRequest.getFollowing().getProfilePic())
                        .build()
        );

        String message = String.format("user %s is following user %s",
                followRequest.getFollower().getUsername(),
                followRequest.getFollowing().getUsername()
        );
        log.info(message);

        return ResponseEntity.ok(new ApiResponse(true, message));
    }

    @GetMapping("/users/{username}/degree")
    public ResponseEntity<?> findNodeDegree(@PathVariable String username) {

        log.info("fetching node degree for user {}", username);
        return ResponseEntity.ok(userService.findNodeDegree(username));
    }

    @GetMapping("/users/{followerUsername}/following/{followingUsername}")
    public ResponseEntity<?> isFollowing(
            @PathVariable String followerUsername,
            @PathVariable String followingUsername
    ) {

        log.info("checking if user {} is following user {}", followerUsername, followingUsername);
        return ResponseEntity.ok(userService.isFollowing(followerUsername, followingUsername));
    }

    @GetMapping("/users/{username}/followers")
    public ResponseEntity<?> findFollowers(@PathVariable String username) {

        log.info("fetching followers for user {}", username);
        return ResponseEntity.ok(userService.findFollowers(username));
    }

    @GetMapping("/users/paginated/{username}/followers")
    public ResponseEntity<?> findPaginatedFollowers(
            @PathVariable String username,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size
    ) {

        log.info("fetching paginated followers for user {}", username);
        return ResponseEntity.ok(userService.findPaginatedFollowers(username, page, size));
    }

    @GetMapping("/users/{username}/following")
    public ResponseEntity<?> findFollowing(@PathVariable String username) {

        log.info("fetching following for user {}", username);
        return ResponseEntity.ok(userService.findFollowing(username));
    }
}
