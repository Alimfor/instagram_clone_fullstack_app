package com.gaziyev.microinstaclone.mediaservice.service;

import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.exception.InvalidFileException;
import com.gaziyev.microinstaclone.mediaservice.exception.InvalidFileNameException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Order(2)
@DisplayName("File Storage Service Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    @Mock
    private Environment environment;

    @InjectMocks
    private FileStorageService underTest;

    private String username;
    private Path path;

    @BeforeEach
    void setUp() throws FileNotFoundException {

        username = "some-username";
        String imagePath = ResourceUtils.getFile("classpath:media/test.jpeg").getPath();
        path = Paths.get(imagePath);
    }

    @AfterAll
    void afterAll() throws IOException {

        final String UPLOAD_DIRECTORY = "D:\\Backend\\pet\\insta-clone-media-service-images\\";
        Path userDir = Paths.get(UPLOAD_DIRECTORY + username);
        FileUtils.deleteDirectory(new File(userDir.toString()));
    }

    @Test
    @Order(1)
    @DisplayName("Trying to store image by username")
    void testStoreFile_givenMultipartFileAndUsername_whenImageIsStoredToDirectory_thenReturnImageMetadata() throws IOException {

        byte[] content = Files.readAllBytes(path);
        final String UPLOAD_DIRECTORY = "D:\\Backend\\pet\\insta-clone-media-service-images\\";
        final String FILE_PATH_PREFIX = "/image/upload";
        final String IMAGE_EXTENSION =
                path.getFileName().toString().substring(
                        path.getFileName().toString()
                                .lastIndexOf('.') + 1
                );

        Path userDir = Paths.get(UPLOAD_DIRECTORY + username);

        MultipartFile file = new MockMultipartFile(
                "some-picture",
                path.getFileName().toString(),
                String.format("image/%s", IMAGE_EXTENSION),
                content
        );

        ReflectionTestUtils.setField(underTest, "uploadDirectory", UPLOAD_DIRECTORY);
        ReflectionTestUtils.setField(underTest, "filePathPrefix", FILE_PATH_PREFIX);

        ImageMetadata imageMetadata = underTest.storeFile(file, username);
        int indexOfDot = imageMetadata.getFilename().lastIndexOf('.');

        assertNotNull(imageMetadata, "Image metadata should not be null");
        assertDoesNotThrow(
                () -> UUID.fromString(
                        imageMetadata.getFilename().substring(0, indexOfDot)
                ),
                "Filename should be UUID"
        );
        assertTrue(
                isValidUri(imageMetadata.getUri()),
                "image uri should be like that http://{hostname}:{post}{filePathPrefix}/{username}/{filename}"
        );
        assertEquals(file.getContentType(), imageMetadata.getFileType(),
                "file type should be the same"
        );
        assertTrue(Files.exists(userDir), "User dir should exist");
    }

    @Test
    @Order(2)
    @DisplayName("Trying to catch InvalidFileException")
    void testStoreFile_givenMultipartFileAndUsername_whenFileIsEmpty_thenThrowInvalidFileException() {

        MultipartFile file = new MockMultipartFile(
                "some-picture",
                path.getFileName().toString(),
                "image/jpg",
                new byte[0]
        );

        assertThrows(InvalidFileException.class,
                () -> underTest.storeFile(file, username),
                "InvalidFileException should be thrown"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Trying to catch InvalidFileNameException")
    void testStoreFile_givenMultipartFileAndUsername_whenFilenameContainDots_thenThrowInvalidFileNameException() throws IOException {

        byte[] content = Files.readAllBytes(path);
        MultipartFile file = new MockMultipartFile(
                "some-picture",
                path.getFileName().toString() + "..",
                "image/jpg",
                content
        );

        assertThrows(InvalidFileNameException.class,
                () -> underTest.storeFile(file, username),
                "InvalidFileNameException should be thrown"
        );
    }


    private boolean isValidUri(String uriString) {

        try {

            URI uri = new URI(uriString);
            return true;
        } catch (URISyntaxException ex) {

            return false;
        }
    }
}
