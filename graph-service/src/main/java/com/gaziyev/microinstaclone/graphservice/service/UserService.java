package com.gaziyev.microinstaclone.graphservice.service;

import com.gaziyev.microinstaclone.graphservice.entity.NodeDegree;
import com.gaziyev.microinstaclone.graphservice.entity.User;
import com.gaziyev.microinstaclone.graphservice.exception.UsernameAlreadyExistsException;
import com.gaziyev.microinstaclone.graphservice.exception.UsernameNotExistsException;
import com.gaziyev.microinstaclone.graphservice.payload.PagedResult;
import com.gaziyev.microinstaclone.graphservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public User createUser(User user) {

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            String message = String.format("User with username %s already exists", user.getUsername());
            log.error(message);

            throw new UsernameAlreadyExistsException(message);
        }

        User savedUser = userRepository.save(user);

        log.info("User created: {}", savedUser);
        return savedUser;
    }

    public User updateUser(User user) {
        return userRepository.findByUsername(user.getUsername())
                .map(projection -> {
                    User savedUser = User.builder()
                            .id(projection.getId())
                            .userId(projection.getUserId())
                            .username(projection.getUsername())
                            .name(projection.getName())
                            .profilePic(projection.getProfilePic())
                            .build();

                    savedUser.setName(user.getName());
                    savedUser.setUsername(user.getUsername());
                    savedUser.setProfilePic(user.getProfilePic());
                    savedUser = userRepository.save(savedUser);
                    log.info("User updated: {} successfully", savedUser.getUsername());

                    return savedUser;
                })
                .orElseThrow(() -> new UsernameNotExistsException(
                        String.format("user with username %s does not exist", user.getUsername())
                ));
    }

    public void follow(User follower, User following) {

        log.info("User {} is following {}", follower.getUsername(), following.getUsername());

        User savedFollower = userRepository
                .findByUserId(follower.getUserId())
                .orElseGet(() -> {
                    log.info("user {} not exists, creating it", follower.getUsername());
                    return this.createUser(follower);
                });

        User savedFollowing = userRepository
                .findByUserId(following.getUserId())
                .orElseGet(() -> {
                    log.info("user {} not exists, creating it", following.getUsername());
                    return this.createUser(following);
                });


        userRepository.follow(savedFollower.getUserId(), savedFollowing.getUserId());
    }

    public NodeDegree findNodeDegree(String username) {

        log.info("fetching node degree for user {}", username);

        long out = userRepository.findOutDegree(username);
        long in = userRepository.findInDegree(username);

        log.info("found {} outdegree and {} indegree for user {}", out, in, username);

        return NodeDegree.builder()
                .outDegree(out)
                .inDegree(in)
                .build();
    }

    public boolean isFollowing(String followerUsername, String followingUsername) {
        return userRepository.isFollowing(followerUsername, followingUsername);
    }

    public List<User> findFollowers(String followerUsername) {

        List<User> followers = userRepository.findFollowers(followerUsername);
        log.info("found {} followers for user {}", followers.size(), followerUsername);

        return followers;
    }

    public PagedResult<User> findPaginatedFollowers(String username, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<User> followers = userRepository.findFollowers(username, pageable);
        log.info("found {} followers for user {}", followers.getTotalElements(), username);

        return buildPagedResult(followers);
    }


    public List<User> findFollowing(String followingUsername) {

        List<User> following = userRepository.findFollowing(followingUsername);
        log.info("found {} following for user {}", following.size(), followingUsername);

        return following;
    }

    private PagedResult<User> buildPagedResult(Page<User> page) {
        return PagedResult.
                <User>builder()
                .page(page.getPageable().getPageNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .contents(page.getContent())
                .build();
    }
}
