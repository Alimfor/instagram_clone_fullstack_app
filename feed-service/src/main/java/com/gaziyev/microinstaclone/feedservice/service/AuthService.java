package com.gaziyev.microinstaclone.feedservice.service;

import com.gaziyev.microinstaclone.feedservice.client.AuthServiceClient;
import com.gaziyev.microinstaclone.feedservice.configuration.JwtConfig;
import com.gaziyev.microinstaclone.feedservice.exception.UnableToGetAccessTokenException;
import com.gaziyev.microinstaclone.feedservice.exception.UnableToGetUsersException;
import com.gaziyev.microinstaclone.feedservice.dto.JwtAuthenticationResponseDTO;
import com.gaziyev.microinstaclone.feedservice.dto.ServiceLoginRequestDTO;
import com.gaziyev.microinstaclone.feedservice.dto.UserSummaryDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.toMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthServiceClient authServiceClient;
    private final ServiceLoginRequestDTO serviceLoginRequest;
    private final JwtConfig jwtConfig;

    public String getAccessToken() {

        ResponseEntity<JwtAuthenticationResponseDTO> response =
                authServiceClient.signIn(serviceLoginRequest);

        if (!response.getStatusCode().is2xxSuccessful()) {
            String message = String.format(
                    "unable to get access token: %s", response.getStatusCode()
            );
            log.error(message);
            throw new UnableToGetAccessTokenException(message);
        }

        return Objects.requireNonNull(response.getBody())
                .getTokens().get("access_token");
    }

    public Map<String, String> getUsersNameWithProfilePic(String token, List<String> usernames) {

        ResponseEntity<List<UserSummaryDTO>> response =
                authServiceClient.findByUsername(jwtConfig.getPrefix() + token, usernames);

        if (!response.getStatusCode().is2xxSuccessful()) {
            String message = String.format(
                    "unable to get user profile pic: %s", response.getStatusCode()
            );
            log.error(message);

            throw new UnableToGetUsersException(message);
        }

        try {
            return Objects.requireNonNull(response.getBody())
                    .stream()
                    .collect(toMap(UserSummaryDTO::getUsername,
                            UserSummaryDTO::getProfilePicture));
        } catch (NullPointerException e) {
            throw new NullPointerException("unable to get user profile picture");
        }
    }
}
