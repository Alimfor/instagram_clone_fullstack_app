package com.gaziyev.microinstaclone.feedservice.client;

import com.gaziyev.microinstaclone.feedservice.dto.JwtAuthenticationResponseDTO;
import com.gaziyev.microinstaclone.feedservice.dto.ServiceLoginRequestDTO;
import com.gaziyev.microinstaclone.feedservice.dto.UserSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(url = "http://localhost:8765/insta", name = "auth-service")
public interface AuthServiceClient {

    String POST_SIGN_IN = "/auth/sign-in";
    String POST_GET_USERS_SUMMARY_BY_USERNAMES = "/users/summary/in";

    @PostMapping(POST_SIGN_IN)
    ResponseEntity<JwtAuthenticationResponseDTO> signIn(
            @RequestBody ServiceLoginRequestDTO serviceLoginRequest
    );

    @PostMapping(POST_GET_USERS_SUMMARY_BY_USERNAMES)
    ResponseEntity<List<UserSummaryDTO>> findByUsername(
            @RequestHeader("Authorization") String token,
            @RequestBody List<String> usernames
    );
}
