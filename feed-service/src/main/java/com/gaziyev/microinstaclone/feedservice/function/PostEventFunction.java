package com.gaziyev.microinstaclone.feedservice.function;

import com.gaziyev.microinstaclone.feedservice.payload.Post;
import com.gaziyev.microinstaclone.feedservice.messaging.PostEventType;
import com.gaziyev.microinstaclone.feedservice.dto.PostEvenPayloadDTO;
import com.gaziyev.microinstaclone.feedservice.service.FeedGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class PostEventFunction {

    private final FeedGeneratorService feedGeneratorService;

    @Bean
    public Consumer<Message<PostEvenPayloadDTO>> receivePostChangedEvent() {
        return payloadMessage -> {

            PostEventType eventType = payloadMessage.getPayload().getEventType();

            log.info("received message to process post {} for user {} event type {}",
                    payloadMessage.getPayload().getId(),
                    payloadMessage.getPayload().getUsername(),
                    eventType
            );

            Acknowledgment acknowledgment =
                    payloadMessage.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT, Acknowledgment.class);

            switch (eventType) {
                case CREATED -> feedGeneratorService.addToFeed(convertTo(payloadMessage.getPayload()));
                case DELETED -> feedGeneratorService.removeFromFeed(convertTo(payloadMessage.getPayload()));
            }

            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        };
    }

    private Post convertTo(PostEvenPayloadDTO payload) {
        return Post.builder()
                .id(payload.getId())
                .username(payload.getUsername())
                .imageUrl(payload.getImageUrl())
                .createdAt(payload.getCreatedAt())
                .build();
    }
}
