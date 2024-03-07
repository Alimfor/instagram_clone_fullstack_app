package com.gaziyev.microinstaclone.apigateway.util;

import lombok.Getter;

@Getter
public enum ServiceName {

    AUTH_SERVICE ("auth"),
    FEED_SERVICE ("feed"),
    GRAPH_SERVICE ("graph"),
    MEDIA_SERVICE ("media"),
    POST_SERVICE ("post");

    private final String name;

    ServiceName(String serviceName) {
        this.name = serviceName;
    }
}
