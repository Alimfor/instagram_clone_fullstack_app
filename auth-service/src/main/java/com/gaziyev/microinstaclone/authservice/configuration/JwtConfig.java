package com.gaziyev.microinstaclone.authservice.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@Component
public class JwtConfig {

    @Value("${security.jwt.header}")
    private String header;

    @Value("${security.jwt.prefix}")
    private String prefix;

    @Value("${security.jwt.subject}")
    private String subject;

    @Value("${security.jwt.issuer}")
    private String issuer;

    @Value("${security.jwt.expiration}")
    private int expiration;

    @Value("${security.jwt.secret}")
    private String secret;
}
