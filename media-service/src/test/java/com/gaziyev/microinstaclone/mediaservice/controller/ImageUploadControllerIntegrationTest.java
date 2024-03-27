package com.gaziyev.microinstaclone.mediaservice.controller;

import com.gaziyev.microinstaclone.mediaservice.dto.UploadFileResponseDTO;
import com.gaziyev.microinstaclone.mediaservice.repository.ImageMetadataRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@Order(5)
@DisplayName("Image Upload Controller Integration Tests")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ImageUploadControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private ImageMetadataRepository repository;

    private HttpHeaders headers;
    MultiValueMap<String, HttpEntity<?>> body;
    private static final String POST_IMAGE = "/image/upload";

    @BeforeEach
    void setUp() throws IOException {

        headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        final String IMAGE_PATH = ResourceUtils.getFile("classpath:media/test.jpeg").getPath();
        Path path = Paths.get(IMAGE_PATH);

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "some-picture.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                Files.readAllBytes(path)
        );

        ByteArrayResource resource = new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };

        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("image", resource);

        body = builder.build();
    }

    @Test
    @Order(1)
    @DisplayName("Trying to upload image without jwt token")
    void testUploadFile_givenMultipartFileAndAuthentication_whenJwtIsNotProvided_thenReturn401() {

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", "some-picture.jpg".getBytes());

        var request = new HttpEntity<>(body, headers);

        ResponseEntity<?> response = testRestTemplate.postForEntity(
                POST_IMAGE,
                request,
                UploadFileResponseDTO.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    @Order(2)
    @DisplayName("Trying to upload image with jwt token")
    void testUploadFile_givenMultipartFileAndAuthentication_whenFileIsUploaded_thenReturnUploadFileResponseDTO() throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader("../jwt.txt"));
        String jwt = reader.readLine();
        reader.close();

        headers.setBearerAuth(jwt);

        var request = new HttpEntity<>(body, headers);

        ResponseEntity<?> response = testRestTemplate.postForEntity(
                POST_IMAGE,
                request,
                UploadFileResponseDTO.class
        );

        UploadFileResponseDTO actual = (UploadFileResponseDTO) response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(actual);
    }
}
