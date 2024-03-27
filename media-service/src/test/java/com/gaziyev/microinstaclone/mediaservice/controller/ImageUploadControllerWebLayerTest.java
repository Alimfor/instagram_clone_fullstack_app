package com.gaziyev.microinstaclone.mediaservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaziyev.microinstaclone.mediaservice.dto.UploadFileResponseDTO;
import com.gaziyev.microinstaclone.mediaservice.entity.ImageMetadata;
import com.gaziyev.microinstaclone.mediaservice.service.ImageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(
        controllers = ImageUploadController.class
)
@AutoConfigureDataMongo
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
@DisplayName("Image Upload Controller Web Layer Test")
public class ImageUploadControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @Test
    @Order(1)
    @DisplayName("Trying to upload file")
    @WithMockUser(username = "testUser", roles = "USER")
    void testUploadFile_givenMultipartFileAndAuthentication_whenFileIsUploaded_thenReturnUploadFileResponseDTO() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "image",
                "some-picture.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some picture".getBytes()
        );

        ImageMetadata expected = new ImageMetadata(
                "test.jpg", "/test.jpg", "image/jpeg"
        );

        when(imageService.upload(file, "testUser"))
                .thenReturn(expected);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/image/upload")
                .file(file)
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isOk())
                .andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        UploadFileResponseDTO actual
                = new ObjectMapper().readValue(responseBodyAsString, UploadFileResponseDTO.class);

        assertEquals(expected.getFilename(), actual.getFilename()
                ,"Expected filename should be the same as actual filename"
        );
        assertEquals(expected.getUri(), actual.getUri()
                ,"Expected uri should be the same as actual uri"
        );
        assertEquals(expected.getFileType(), actual.getFileType()
                ,"Expected file type should be the same as actual file type"
        );
    }

    @Test
    @Order(2)
    @DisplayName("Trying to upload file with wrong request file name")
    @WithMockUser(username = "testUser", roles = "USER")
    void testUploadFile_givenWrongMultipartFileName_whenIsRejected_thenReturn400AndErrorMessage() throws Exception {

        final String EXPECTED_MSG = "Required part 'image' is not present.";
        MockMultipartFile file = new MockMultipartFile(
                "wrong-name",
                "some-picture.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "some picture".getBytes()
        );

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart("/image/upload")
                .file(file)
                .with(SecurityMockMvcRequestPostProcessors.csrf());

        MvcResult mvcResult = mockMvc.perform(requestBuilder)
                .andExpect(status().isBadRequest())
                .andReturn();

        String actual = mvcResult.getResolvedException().getMessage();

        verify(imageService, never()).upload(file, "testUser");
        assertEquals(EXPECTED_MSG, actual,
                "Expected message should be the same as actual message"
        );
    }
}
