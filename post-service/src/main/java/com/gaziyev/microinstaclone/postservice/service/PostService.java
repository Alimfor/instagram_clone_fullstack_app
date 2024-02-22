package com.gaziyev.microinstaclone.postservice.service;

import com.gaziyev.microinstaclone.postservice.entity.Post;
import com.gaziyev.microinstaclone.postservice.exception.NotAllowedException;
import com.gaziyev.microinstaclone.postservice.exception.ResourceNotFoundException;
import com.gaziyev.microinstaclone.postservice.messaging.PostEventSender;
import com.gaziyev.microinstaclone.postservice.dto.PostRequestDTO;
import com.gaziyev.microinstaclone.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final PostEventSender postEventSender;

	public Post createPost(PostRequestDTO postRequest) {
		log.info("creating post image url: {}", postRequest.getImageUrl());

		Post post = new Post(postRequest.getImageUrl(), postRequest.getCaption());

		post = postRepository.save(post);
		postEventSender.sendPostCreated(post);

		log.info("post {} is saved successfully for user {}",
		         post.getId(), post.getUsername()
		);

		return post;
	}

	public void deletePost(String postId, String username) {
		log.info("deleting post {} for user {}", postId, username);

		postRepository
				.findById(postId)
				.map(post -> {
					if (!post.getUsername().equals(username)) {
						log.warn("user {} is not allowed to delete post id {}",
								username, postId
						);
						throw new NotAllowedException(
								username, "post id " + postId, "delete"
						);
					}

					postRepository.delete(post);
					postEventSender.sendPostDeleted(post);
					return post;
				})
				.orElseThrow(() -> {
					log.warn("post not found id {}", postId);
					return new ResourceNotFoundException(postId);
				});
	}

	@Transactional(readOnly = true)
	public List<Post> postsByUsername(String username) {
		return postRepository.findByUsernameOrderByCreatedAtDesc(username);
	}

	@Transactional(readOnly = true)
	public List<Post> postsByIdIn(List<String> ids) {
		return postRepository.findByIdInOrderByCreatedAtDesc(ids);
	}
}
