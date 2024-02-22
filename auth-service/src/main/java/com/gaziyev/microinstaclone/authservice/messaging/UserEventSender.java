package com.gaziyev.microinstaclone.authservice.messaging;

import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.dto.UserEventPayloadDTO;
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
public class UserEventSender {

    private final StreamBridge streamBridge;

    public void sendUserCreated(User user) {
        log.info("sending user created event for user {}", user.getUsername());
        sendUserChangeEvent(convertTo(user, UserEventType.CREATED));
    }

    public void sendUserUpdated(User user) {
        log.info("sending user updated event for user {}", user.getUsername());
        sendUserChangeEvent(convertTo(user, UserEventType.UPDATED));
    }

    public void sendUserUpdated(User user, String oldPictureUrl) {
        log.info("the profile picture is updated for user {}", user.getUsername());
        UserEventPayloadDTO userEventPayload = convertTo(user, UserEventType.UPDATED);
        userEventPayload.setOldProfilePictureUrl(oldPictureUrl);

        sendUserChangeEvent(userEventPayload);
    }

    private void sendUserChangeEvent(UserEventPayloadDTO userEventPayload) {
        Message<UserEventPayloadDTO> message = MessageBuilder
                .withPayload(userEventPayload)
                .setHeader(KafkaHeaders.RECEIVED_KEY, userEventPayload.getId())
                .build();

        boolean result = streamBridge.send("sendUserChangedEvent-out-0", message);
        log.info("Is the Communication request successful? {}", result);
    }

    private UserEventPayloadDTO convertTo(User user, UserEventType eventType) {
        return UserEventPayloadDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getUserProfile().getDisplayName())
                .profilePictureUrl(user.getUserProfile().getProfilePictureUrl())
                .eventType(eventType)
                .build();
    }
}
