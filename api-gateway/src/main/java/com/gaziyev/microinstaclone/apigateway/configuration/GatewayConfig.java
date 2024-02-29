package com.gaziyev.microinstaclone.apigateway.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service-route", gate ->
                        gate.path("/insta/auth/**")
                                .filters(filter -> filter
                                        .stripPrefix(2)
                                        .filter(authenticationFilter)
                                )
                                .uri("lb://auth-service")
                )
                .route("feed-service-route", gate -> gate
                        .path("/insta/feed/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/feed/(?<segment>.*)", "/feed/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://feed-service")
                )
                .route("graph-service-route", gate -> gate
                        .path("/insta/graph/**")
                        .filters(filter -> filter
                                .stripPrefix(2)
                                .filter(authenticationFilter)
                        )
                        .uri("lb://graph-service")
                )
                .route("media-service-route", gate -> gate
                        .path("/insta/media/**")
                        .filters(filter -> filter
                                .stripPrefix(2)
                                .filter(authenticationFilter)
                        )
                        .uri("lb://media-service")
                )
                .route("post-service-route", gate -> gate
                        .path("/insta/post/**")
                        .filters(filter -> filter
                                .stripPrefix(2)
                                .filter(authenticationFilter)
                        )
                        .uri("lb://post-service")
                )
                .build();
    }
}
