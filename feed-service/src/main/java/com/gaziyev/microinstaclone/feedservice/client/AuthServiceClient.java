package com.gaziyev.microinstaclone.feedservice.client;

import com.gaziyev.microinstaclone.feedservice.payload.JwtAuthenticationResponse;
import com.gaziyev.microinstaclone.feedservice.payload.ServiceLoginRequest;
import com.gaziyev.microinstaclone.feedservice.payload.UserSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "insta-auth")
public interface AuthServiceClient {

    String POST_SIGN_IN = "/sign-in";
    String POST_GET_USERS_SUMMARY_BY_USERNAMES = "/users/summary/in";

    @PostMapping(POST_SIGN_IN)
    ResponseEntity<JwtAuthenticationResponse> signIn(
            @RequestBody ServiceLoginRequest serviceLoginRequest
    );

    @PostMapping(POST_GET_USERS_SUMMARY_BY_USERNAMES)
    ResponseEntity<List<UserSummary>> findByUsername(
        @RequestHeader("Authorization") String token,
        @RequestBody List<String> usernames
    );
}
