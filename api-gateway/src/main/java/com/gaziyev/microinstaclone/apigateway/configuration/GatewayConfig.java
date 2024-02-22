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
                .route(gate -> gate
                        .path("/insta/auth/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/auth/(?<segment>.*)","/auth/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://auth-service")
                )
                .route(gate -> gate
                        .path("/insta/users/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/users/(?<segment>.*)","/users/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://auth-service")
                )
                .route(gate -> gate
                        .path("/insta/feed/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/feed/(?<segment>.*)","/feed/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://feed-service")
                )
                .route(gate -> gate
                        .path("/insta/follow_users/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/follow_users/(?<segment>.*)","/users/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://graph-service")
                )
                .route(gate -> gate
                        .path("/insta/images/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/images/(?<segment>.*)","/images/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://media-service")
                )
                .route(gate -> gate
                        .path("/insta/posts/**")
                        .filters(filter -> filter
                                .rewritePath("/insta/posts/(?<segment>.*)","/posts/${segment}")
                                .filter(authenticationFilter)
                        )
                        .uri("lb://post-service")
                )
                .build();
    }
}
