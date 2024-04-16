package com.gaziyev.microinstaclone.apigateway.configuration;

import com.gaziyev.microinstaclone.apigateway.util.ServiceName;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.Buildable;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.PredicateSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;

import static org.springframework.cloud.gateway.support.RouteMetadataUtils.CONNECT_TIMEOUT_ATTR;
import static org.springframework.cloud.gateway.support.RouteMetadataUtils.RESPONSE_TIMEOUT_ATTR;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        RouteLocatorBuilder.Builder routes = builder.routes();

        for (ServiceName serviceName : ServiceName.values()) {

            routes = routes.route(serviceName.getName().toLowerCase() + "-service-route", gate -> {
                String serviceNameLowerCase = serviceName.getName().toLowerCase();
                String path = String.format("/inst/%s/**", serviceNameLowerCase);
                String lbUri = String.format("lb://%s-service", serviceNameLowerCase);
                String circuitBreakerName = String.format("%s-service-circuit-breaker", serviceNameLowerCase);

                return configureRoute(gate, path, lbUri, circuitBreakerName, serviceNameLowerCase);
            });
        }

        return routes.build();
    }

    private Buildable<Route> configureRoute(
            PredicateSpec route,
            String path, String uri, String circuitBreakerName, String serviceName
    ) {
        return route.path(path)
                .filters(filter -> commonFilters(
                        filter.stripPrefix(2),
                        circuitBreakerName, serviceName))
                .metadata(RESPONSE_TIMEOUT_ATTR, 3000L)
                .metadata(CONNECT_TIMEOUT_ATTR, 1000L)
                .uri(uri);
    }

    private GatewayFilterSpec commonFilters(GatewayFilterSpec filter, String circuitBreakerName, String serviceName) {
        return filter
                .filter(authenticationFilter)
                .circuitBreaker(config -> config
                        .setName(circuitBreakerName)
                        .setFallbackUri("forward:/contact-support/" + serviceName)
                )
                .retry(retryConfig -> retryConfig
                        .setRetries(3)
                        .setMethods(HttpMethod.GET)
                        .setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),
                                2, true
                        )
                );
    }
}
