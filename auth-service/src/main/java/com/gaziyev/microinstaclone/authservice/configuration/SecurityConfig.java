package com.gaziyev.microinstaclone.authservice.configuration;

import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.repository.UserRepository;
import com.gaziyev.microinstaclone.authservice.service.JwtTokenProvider;
import com.gaziyev.microinstaclone.authservice.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {

	private final UserDetailsService instaUserDetailsService;
	private final JwtConfig jwtConfig;
	private final JwtTokenProvider jwtTokenProvider;
	private final String serviceUsername;
	private final String servicePassword;

	public SecurityConfig(
			UserDetailsService instaUserDetailsService,
			JwtConfig jwtConfig, JwtTokenProvider jwtTokenProvider,
			@Value("${security.service.username}") String serviceUsername,
			@Value("${security.service.password}") String servicePassword) {
		this.instaUserDetailsService = instaUserDetailsService;
		this.jwtConfig = jwtConfig;
		this.jwtTokenProvider = jwtTokenProvider;
		this.serviceUsername = serviceUsername;
		this.servicePassword = servicePassword;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth ->
						                       auth
								                       .requestMatchers(HttpMethod.POST, "/sign-in").permitAll()
								                       .requestMatchers(HttpMethod.POST, "/users").anonymous()
								                       .anyRequest().authenticated()
				)
				.sessionManagement(session ->
						                   session.sessionCreationPolicy(
								                   SessionCreationPolicy.STATELESS)
				)
				.exceptionHandling(exception ->
						                   exception.authenticationEntryPoint(
								                   ((request, response, authException) -> {
									                   response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
								                   })
						                   )
				)
				.addFilterBefore(
						new JwtTokenAuthenticationFilter(jwtConfig, jwtTokenProvider, instaUserDetailsService, serviceUsername),
						UsernamePasswordAuthenticationFilter.class
				);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration configuration
	) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();

		UserDetails serviceUser = User.withUsername(serviceUsername)
				.password(passwordEncoder().encode(servicePassword))
				.roles(Role.SERVICE.getName())
				.build();

		authenticationProvider.setUserDetailsService(new InMemoryUserDetailsManager(serviceUser));

		authenticationProvider.setUserDetailsService(instaUserDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		return authenticationProvider;
	}
}
