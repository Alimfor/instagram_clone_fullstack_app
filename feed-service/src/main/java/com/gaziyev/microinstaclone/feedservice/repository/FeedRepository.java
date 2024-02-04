package com.gaziyev.microinstaclone.feedservice.repository;

import com.gaziyev.microinstaclone.feedservice.entity.UserFeed;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@EnableRedisRepositories
public interface FeedRepository extends CrudRepository<UserFeed, String> {

    Slice<UserFeed> findByUsername(String username, Pageable pageable);
    UserFeed findByUsername(String username);
}
