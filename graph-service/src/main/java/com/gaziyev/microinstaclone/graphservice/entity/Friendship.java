package com.gaziyev.microinstaclone.graphservice.entity;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;


@Builder
@RelationshipProperties
public class Friendship {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private User targetUser;
}
