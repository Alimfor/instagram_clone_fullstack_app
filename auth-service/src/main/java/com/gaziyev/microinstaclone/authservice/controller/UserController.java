package com.gaziyev.microinstaclone.authservice.controller;

import com.gaziyev.microinstaclone.authservice.dto.*;
import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.exception.BadRequestException;
import com.gaziyev.microinstaclone.authservice.exception.EmailAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.exception.ResourceNotFoundException;
import com.gaziyev.microinstaclone.authservice.exception.UsernameAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.model.InstaUserDetails;
import com.gaziyev.microinstaclone.authservice.service.JwtTokenService;
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
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenProvider;

    private static final String POST_SIGN_IN = "/sign-in";
    private static final String POST_REFRESH_TOKEN = "/refresh-token";
    private static final String POST_SIGN_UP = "/sign-up";
    private static final String GET_USER_BY_USERNAME = "/users/{username}";
    private static final String GET_ALL_USERS = "/users/all";
    private static final String GET_CURRENT_USER = "/users/me";
    private static final String GET_USER_SUMMARY_BY_USERNAME = "/users/summary/{username}";
    private static final String POST_FIND_USERS_SUMMARIES_BY_USERNAMES = "/users/summary/in";
    private static final String PUT_UPLOAD_PROFILE_PICTURE_FOR_CURRENT_USER = "/users/me/picture";

    @PostMapping(POST_SIGN_IN)
    public ResponseEntity<?> authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Map<String, String> jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponseDTO(jwt));
    }

    @PostMapping(POST_REFRESH_TOKEN)
    public ResponseEntity<?> refreshToken(
            @Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest
    ) {

        String token = refreshTokenRequest.getRefreshToken();
        if (!jwtTokenProvider.validateToken(token, true)) {
            throw new BadRequestException("Invalid refresh token");
        }

        Map<String, String> newJwt = jwtTokenProvider.refreshToken(token);
        return ResponseEntity.ok(new JwtAuthenticationResponseDTO(newJwt));
    }

    @PostMapping(value = POST_SIGN_UP, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@Valid @RequestBody SignUpRequestDTO payload) {
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
                .body(new ApiResponseDTO(true, "User registered successfully"));
    }

    @PutMapping(PUT_UPLOAD_PROFILE_PICTURE_FOR_CURRENT_USER)
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestBody String profilePicture,
            @AuthenticationPrincipal InstaUserDetails instaUserDetails
    ) {

        userService.updateProfilePicture(profilePicture, instaUserDetails.getId());

        return ResponseEntity
                .ok()
                .body(new ApiResponseDTO(true, "Profile picture updated successfully"));
    }

    @GetMapping(value = GET_USER_BY_USERNAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUser(@PathVariable String username) {
        log.info("retrieving user: " + username);

        return userService
                .findUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    @GetMapping(value = GET_ALL_USERS, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAll() {
        log.info("retrieving all users");

        return ResponseEntity
                .ok(userService.findAll());
    }

    @GetMapping(value = GET_CURRENT_USER, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.OK)
    public UserSummaryDTO getCurrentUser(@AuthenticationPrincipal InstaUserDetails instaUserDetails) {
        return UserSummaryDTO.builder()
                .id(instaUserDetails.getId())
                .username(instaUserDetails.getUsername())
                .name(instaUserDetails.getUsername())
                .profilePicture(instaUserDetails.getUserProfile().getProfilePictureUrl())
                .build();
    }

    @GetMapping(value = GET_USER_SUMMARY_BY_USERNAME, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserSummary(@PathVariable("username") String username) {
        log.info("retrieving user {}", username);

        return userService
                .findUserByUsername(username)
                .map(user -> ResponseEntity.ok(convertTo(user)))
                .orElseThrow(() -> new ResourceNotFoundException(username));
    }

    @PostMapping(value = POST_FIND_USERS_SUMMARIES_BY_USERNAMES, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUserSummaries(@RequestBody List<String> usernames) {
        log.info("retrieving summaries for {} usernames", usernames.size());

        List<UserSummaryDTO> summaries =
                userService
                        .findByUsernameIn(usernames)
                        .stream()
                        .map(this::convertTo)
                        .toList();

        return ResponseEntity.ok(summaries);
    }

    private UserSummaryDTO convertTo(User user) {
        return UserSummaryDTO
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getUserProfile().getDisplayName())
                .profilePicture(user.getUserProfile().getProfilePictureUrl())
                .build();
    }
}
