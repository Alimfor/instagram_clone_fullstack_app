package com.gaziyev.microinstaclone.graphservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NodeDegree {

    long outDegree;
    long inDegree;
}
