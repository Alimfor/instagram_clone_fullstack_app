package com.gaziyev.microinstaclone.authservice.controller;

import com.gaziyev.microinstaclone.authservice.entity.InstaUserDetails;
import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.exception.BadRequestException;
import com.gaziyev.microinstaclone.authservice.exception.EmailAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.exception.ResourceNotFoundException;
import com.gaziyev.microinstaclone.authservice.exception.UsernameAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.payload.*;
import com.gaziyev.microinstaclone.authservice.service.JwtTokenProvider;
import com.gaziyev.microinstaclone.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/sign-in")
	public ResponseEntity<?> authenticateUser(
			@Valid @RequestBody LoginRequest loginRequest
	) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						loginRequest.getUsername(),
						loginRequest.getPassword()
				)
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtTokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

	@PostMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequest payload) {
		log.info("createUser: " + payload.getUsername());

		User user = User.builder()
				.username(payload.getUsername())
				.email(payload.getEmail())
				.password(payload.getPassword())
				.userProfile(Profile.builder()
						             .displayName(payload.getName())
						             .build())
				.build();

		try {
			userService.registerUser(user);
		} catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {
			throw new BadRequestException(e.getMessage());
		}

		URI location = ServletUriComponentsBuilder
				.fromCurrentContextPath().path("/users/{username}")
				.buildAndExpand(user.getUsername()).toUri();

		return ResponseEntity
				.created(location)
				.body(new ApiResponse(true, "User registered successfully"));
	}

	@PutMapping("/users/me/pictures")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> uploadProfilePicture(
			@RequestBody String profilePicture,
			@AuthenticationPrincipal InstaUserDetails instaUserDetails
	) {

		userService.updateProfilePicture(profilePicture, instaUserDetails.getId());

		return ResponseEntity
				.ok()
				.body(new ApiResponse(true, "Profile picture updated successfully"));
	}

	@GetMapping(value = "/users/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findUser(@PathVariable String username) {
		log.info("retrieving user: " + username);

		return userService
				.findUserByUsername(username)
				.map(user -> ResponseEntity.ok(user))
				.orElseThrow(() -> new ResourceNotFoundException(username));
	}

	@GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findAll() {
		log.info("retrieving all users");

		return ResponseEntity
				.ok(userService.findAll());
	}

	@GetMapping(value = "/users/me", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorize("hasRole('USER')")
	@ResponseStatus(HttpStatus.OK)
	public UserSummary getCurrentUser(@AuthenticationPrincipal InstaUserDetails instaUserDetails) {
		return UserSummary.builder()
				.id(instaUserDetails.getId())
				.username(instaUserDetails.getUsername())
				.name(instaUserDetails.getUsername())
				.profilePicture(instaUserDetails.getUserProfile().getProfilePictureUrl())
				.build();
	}

	@GetMapping(value = "/users/summary/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserSummary(@PathVariable("username") String username) {
		log.info("retrieving user {}", username);

		return  userService
				.findUserByUsername(username)
				.map(user -> ResponseEntity.ok(convertTo(user)))
				.orElseThrow(() -> new ResourceNotFoundException(username));
	}

	@PostMapping(value = "/users/summary/in", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserSummaries(@RequestBody List<String> usernames) {
		log.info("retrieving summaries for {} usernames", usernames.size());

		List<UserSummary> summaries =
				userService
						.findByUsernameIn(usernames)
						.stream()
						.map(this::convertTo)
						.toList();

		return ResponseEntity.ok(summaries);

	}

	private UserSummary convertTo(User user) {
		return UserSummary
				.builder()
				.id(user.getId())
				.username(user.getUsername())
				.name(user.getUserProfile().getDisplayName())
				.profilePicture(user.getUserProfile().getProfilePictureUrl())
				.build();
	}
}
