package com.gaziyev.microinstaclone.graphservice.repository;

import com.gaziyev.microinstaclone.graphservice.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends Neo4jRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByUsername(String username);

    @Query("MATCH (n)-[r]->() where n.username=$username RETURN COUNT(r)")
    long findOutDegree(String username);

    @Query("MATCH (n)<-[r]-() where n.username=$username RETURN COUNT(r)")
    long findInDegree(String username);

    @Query("MATCH (n1:User{ username:$username1 }), (n2:User{username:$username2 }) RETURN EXISTS((n1)-[:IS_FOLLOWING]->(n2))")
    boolean isFollowing(String username1, String username2);

    @Query("MATCH (n:User{username:$username})<--(f:User) RETURN f")
    List<User> findFollowers(String username);

    @Query(value = "MATCH (n:User{username:$username})<--(f:User) RETURN f",
    countQuery = "MATCH (n:User{username:$username})<--(f:User) RETURN count(f)")
    Page<User> findFollowers(String username, Pageable pageable);

    @Query("MATCH (n:User{username:$username})-->(f:User) RETURN f")
    List<User> findFollowing(String username);

}
