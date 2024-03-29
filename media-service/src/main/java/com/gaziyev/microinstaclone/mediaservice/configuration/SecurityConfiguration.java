package com.gaziyev.microinstaclone.mediaservice.configuration;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfiguration {

	private final JwtConfig jwtConfig;

	@Value("${file.path.prefix}")
	private String filePathPrefix;


	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth ->
						                       auth
								                       .requestMatchers(filePathPrefix + "/**", "/actuator/**").permitAll()
								                       .anyRequest().authenticated()
				)
				.sessionManagement(session ->
						                   session
								                   .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(exception ->
						exception
								.authenticationEntryPoint(
										(request, response, authException) -> {
											response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
										}
								)
				)
				.addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig),
				                UsernamePasswordAuthenticationFilter.class
				);

		return http.build();
	}
}
