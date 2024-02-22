package com.gaziyev.microinstaclone.postservice.controller;

import com.gaziyev.microinstaclone.postservice.entity.Post;
import com.gaziyev.microinstaclone.postservice.dto.ApiResponseDTO;
import com.gaziyev.microinstaclone.postservice.dto.PostRequestDTO;
import com.gaziyev.microinstaclone.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	private static final String POST_CREATE_POST = "/posts/create";
	private static final String DELETE_POST_BY_ID = "/posts/{id}";
	private static final String GET_CURRENT_USER_POSTS ="/posts/me";
	private static final String GET_USER_POSTS = "/posts/{username}";
	private static final String POST_FIND_POSTS_BY_IDS = "/posts/in";

	@PostMapping(POST_CREATE_POST)
	public ResponseEntity<?> createPost(@RequestBody PostRequestDTO postRequest) {
		log.info("received a request to create a post for image {}",
		         postRequest.getImageUrl()
		);

		Post post = postService.createPost(postRequest);

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/posts/{id}")
				.buildAndExpand(post.getId()).toUri();

		return ResponseEntity
				.created(location)
				.body(new ApiResponseDTO(true, "Post created successfully"));
	}

	@DeleteMapping(DELETE_POST_BY_ID)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePost(@PathVariable String id,
	                       Authentication authentication
	) {
		log.info("received a delete request for post id {} from user {}",
		         id, authentication.getPrincipal().toString()
		);
		postService.deletePost(id, authentication.getPrincipal().toString());
	}

	@GetMapping(GET_CURRENT_USER_POSTS)
	public ResponseEntity<?> findCurrentUserPosts(
			Authentication authentication
	) {
		log.info("retrieving posts for user {}", authentication.getPrincipal().toString());

		List<Post> posts = postService.postsByUsername(authentication.getPrincipal().toString());
		log.info("found {} posts for user {}",
		         posts.size(), authentication.getPrincipal().toString()
		);

		return ResponseEntity.ok(posts);
	}

	@GetMapping(GET_USER_POSTS)
	public ResponseEntity<?> findUserPosts(@PathVariable String username) {
		log.info("retrieving posts for user {}", username);

		List<Post> posts = postService.postsByUsername(username);
		log.info("found {} posts for user {}", posts.size(), username);

		return ResponseEntity.ok(posts);
	}

	@PostMapping(POST_FIND_POSTS_BY_IDS)
	public ResponseEntity<?> findPostsByIdIn(@RequestBody List<String> ids) {
		log.info("retrieving posts for ids {}", ids.size());

		List<Post> posts = postService.postsByIdIn(ids);
		log.info("found {} posts", posts.size());

		return ResponseEntity.ok(posts);
	}
}
