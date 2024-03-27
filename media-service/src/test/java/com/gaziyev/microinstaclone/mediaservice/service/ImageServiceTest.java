package com.gaziyev.microinstaclone.mediaservice.service;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.repository.ImageMetadataRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@Order(3)
@DisplayName("Image Service Tests")
@ExtendWith(MockitoExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageServiceTest {

    @Mock
    private FileStorageService fileStorageService;

    @Mock
    private ImageMetadataRepository repository;

    @InjectMocks
    private ImageService underTest;

    private String username;
    private Path path;

    @BeforeEach
    void setUp() throws FileNotFoundException {

        username = "some-username";
        String imagePath = ResourceUtils.getFile("classpath:media/test.jpeg").getPath();
        path = Paths.get(imagePath);
    }

    @Test
    @Order(1)
    @DisplayName("Trying to upload image")
    void testUpload_givenMultipartFileAndUsername_whenIsUploaded_thenReturnImageMetadata() throws IOException {

        byte[] content = Files.readAllBytes(path);
        final String IMAGE_EXTENSION =
                path.getFileName().toString().substring(
                        path.getFileName().toString()
                                .lastIndexOf('.') + 1
                );

        MultipartFile file = new MockMultipartFile(
                "some-picture",
                path.getFileName().toString(),
                String.format("image/%s", IMAGE_EXTENSION),
                content
        );

        ImageMetadata metadata = new ImageMetadata(
                "random-uuid." + IMAGE_EXTENSION,
                "some-file-url",
                file.getContentType()
        );

        when(fileStorageService.storeFile(any(MultipartFile.class), any(String.class)))
                .thenReturn(metadata);

        when(repository.save(any(ImageMetadata.class)))
                .thenReturn(metadata);

        ImageMetadata imageMetadata = underTest.upload(file, username);

        assertNotNull(imageMetadata, "Image metadata should not be null");
        assertEquals(metadata.getUsername(), imageMetadata.getUsername(),
            "Username should be the same"
        );
        assertEquals(metadata.getFilename(), imageMetadata.getFilename(),
            "Filename should be the same"
        );
        assertEquals(metadata.getUri(), imageMetadata.getUri(),
            "URI should be the same"
        );
    }
}
