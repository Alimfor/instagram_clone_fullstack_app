package com.gaziyev.microinstaclone.apigateway.configuration;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Predicate;

@Service
public class RouteValidator {

    public static final List<String> openEndpoints = List.of(
            "/sign-in",
            "/sign-up",
            "/refresh-token",
            "/actuator"
    );

    public Predicate<ServerHttpRequest> isSecured =
            request -> openEndpoints.stream()
                    .noneMatch(uri -> request.getURI()
                            .getPath()
                            .contains(uri)
                    );
}
