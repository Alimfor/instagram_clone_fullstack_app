package com.gaziyev.microinstaclone.graphservice.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.Set;

@Getter
@Setter
@Builder
@Node("User")
public class User {

    @Id
    @GeneratedValue
    private Long id;
    private String userId;
    private String username;
    private String name;
    private String profilePic;

    @Relationship(type = "IS_FOLLOWING")
    private Set<Friendship> friendships;
}
