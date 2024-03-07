package com.gaziyev.microinstaclone.apigateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/contact-support/{serviceName}")
    public Mono<ResponseEntity<String>> contactSupport(@PathVariable String serviceName) {
        return Mono.just(
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(String.format(
                                        "An error occurred with %s. Please try after some time or contact support team!",
                                        serviceName
                                )
                        )
        );
    }
}
