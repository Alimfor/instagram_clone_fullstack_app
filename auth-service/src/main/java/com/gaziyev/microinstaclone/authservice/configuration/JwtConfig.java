package com.gaziyev.microinstaclone.authservice.configuration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@NoArgsConstructor
@Component
public class JwtConfig {

	@Value("${security.jwt.uri:/auth/**}")
	private String uri;

	@Value("${security.jwt.header:Authorization}")
	private String header;

	@Value("${security.jwt.prefix}")
	private String prefix;

	@Value("${security.jwt.subject}")
	private String subject;

	@Value("${security.jwt.issuer:auth-service}")
	private String issuer;

	@Value("${security.jwt.expiration:#{24*60*60}}")
	private int expiration;

	@Value("${security.jwt.secret:JwtSecretKey}")
	private String secret;
}
