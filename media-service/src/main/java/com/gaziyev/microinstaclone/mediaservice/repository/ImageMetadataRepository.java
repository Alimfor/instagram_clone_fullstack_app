package com.gaziyev.microinstaclone.mediaservice.repository;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageMetadataRepository extends MongoRepository<ImageMetadata, String> {
}
