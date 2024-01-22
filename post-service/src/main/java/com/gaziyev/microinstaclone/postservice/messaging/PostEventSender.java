package com.gaziyev.microinstaclone.postservice.messaging;

import com.gaziyev.microinstaclone.postservice.entity.Post;
import com.gaziyev.microinstaclone.postservice.payload.PostEventPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostEventSender {

	private final StreamBridge streamBridge;

	public void sendPostCreated(Post post) {

		log.info("sending post created event for post {}", post.getId());
		sendPostChangedEvent(convertTo(post, PostEventType.CREATED));
	}

	public void sendPostUpdated(Post post) {

		log.info("sending post updated event for post {}", post.getId());
		sendPostChangedEvent(convertTo(post, PostEventType.UPDATED));
	}

	public void sendPostDeleted(Post post) {

		log.info("sending post deleted event for post {}", post.getId());
		sendPostChangedEvent(convertTo(post, PostEventType.DELETED));
	}

	public void sendPostChangedEvent(PostEventPayload eventPayload) {

		Message<PostEventPayload> message = MessageBuilder
				.withPayload(eventPayload)
				.setHeader(KafkaHeaders.RECEIVED_KEY, eventPayload.getId())
				.build();

		streamBridge.send("sendPostChangedEvent-out-0", message);
		log.info("post event {} sent to topic {} for post {} and user {}",
		         message.getPayload().getEventType().name(),
		         "sendPostChangedEvent-out-0",
		         message.getPayload().getId(),
		         message.getPayload().getUsername());

	}

	private PostEventPayload convertTo(Post post, PostEventType postEventType) {
		return PostEventPayload.builder()
				.id(post.getId())
				.username(post.getUsername())
				.imageUrl(post.getImageUrl())
				.caption(post.getCaption())
				.eventType(postEventType)
				.lastModifiedBy(post.getLastModifiedBy())
				.createdAt(post.getCreatedAt())
				.updatedAt(post.getUpdatedAt())
				.build();
	}
}
