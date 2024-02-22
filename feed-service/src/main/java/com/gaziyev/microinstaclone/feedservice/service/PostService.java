package com.gaziyev.microinstaclone.feedservice.service;

import com.gaziyev.microinstaclone.feedservice.client.PostServiceClient;
import com.gaziyev.microinstaclone.feedservice.configuration.JwtConfig;
import com.gaziyev.microinstaclone.feedservice.payload.Post;
import com.gaziyev.microinstaclone.feedservice.exception.UnableToGetPostsException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {

    private final PostServiceClient postServiceClient;
    private final JwtConfig jwtConfig;

    public List<Post> findPostsIn(String token, List<String> postIds) {

        log.info("fetching posts in {}", postIds);

        ResponseEntity<List<Post>> response =
                postServiceClient.findPostsByIdIn(jwtConfig.getPrefix() + token, postIds);

        if (!response.getStatusCode().is2xxSuccessful()) {
            String message = String.format("Failed to fetch posts in %s", postIds);
            log.error(message);

            throw new UnableToGetPostsException(message);
        }

        return Objects.requireNonNull(
                response.getBody()
        );
    }
}
