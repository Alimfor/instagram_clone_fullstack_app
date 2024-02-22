package com.gaziyev.microinstaclone.feedservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@NoArgsConstructor
@Component
public class ServiceLoginRequestDTO {

    @Value("${security.service.username}")
    private String username;

    @Value("${security.service.password}")
    private String password;
}
