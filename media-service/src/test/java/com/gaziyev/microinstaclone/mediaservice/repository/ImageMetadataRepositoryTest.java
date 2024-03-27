package com.gaziyev.microinstaclone.mediaservice.repository;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import static org.assertj.core.api.Assertions.*;

@Order(1)
@DisplayName("Image Metadata Repository Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
class ImageMetadataRepositoryTest {

    @Autowired
    private ImageMetadataRepository repository;

    @AfterAll
    void afterAll() {

        repository.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Trying to save image metadata object")
    void testSave_givenImageMetadata_whenSaved_thenReturnSavedImageMetadata() {

        final String EXTENSION = "png";
        final String FILENAME = String.format("some-random-uuid.%s", EXTENSION);
        final String URI = "http://localhost:8080/images/upload/some-username/" + FILENAME;
        final String FILETYPE = "png";
        ImageMetadata imageMetadata = new ImageMetadata(FILENAME, URI, FILETYPE);

        ImageMetadata savedImageMetadata = repository.save(imageMetadata);

        assertThat(savedImageMetadata).isNotNull();
        assertThat(savedImageMetadata.getId()).isNotEmpty();
    }
}
