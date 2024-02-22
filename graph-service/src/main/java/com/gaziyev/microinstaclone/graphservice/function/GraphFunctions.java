package com.gaziyev.microinstaclone.graphservice.function;

import com.gaziyev.microinstaclone.graphservice.entity.User;
import com.gaziyev.microinstaclone.graphservice.messaging.UserEventType;
import com.gaziyev.microinstaclone.graphservice.dto.UserEventPayloadDTO;
import com.gaziyev.microinstaclone.graphservice.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class GraphFunctions {

    @Bean
    public Consumer<Message<UserEventPayloadDTO>> sentUserChangedEvent(UserService userService) {
        return userEventPayload -> {
            UserEventType eventType = userEventPayload.getPayload().getEventType();

            log.info("received message to process user {} eventType {}",
                    userEventPayload.getPayload().getUsername(), eventType.name());

            Acknowledgment acknowledgment =
                    userEventPayload.getHeaders().get(KafkaHeaders.ACKNOWLEDGMENT,
                            Acknowledgment.class);

            User user = convertTo(userEventPayload.getPayload());

            switch (eventType) {
                case CREATED -> userService.createUser(user);
                case UPDATED -> userService.updateUser(user);
            }

            if (acknowledgment != null) {
                acknowledgment.acknowledge();
            }
        };
    }

    private User convertTo(UserEventPayloadDTO payload) {
        return User.builder()
                .userId(payload.getId())
                .username(payload.getUsername())
                .name(payload.getDisplayName())
                .profilePic(payload.getProfilePictureUrl())
                .build();
    }
}
