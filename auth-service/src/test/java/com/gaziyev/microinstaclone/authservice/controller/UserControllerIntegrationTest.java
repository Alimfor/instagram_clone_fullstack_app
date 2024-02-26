package com.gaziyev.microinstaclone.authservice.controller;

import com.gaziyev.microinstaclone.authservice.dto.ApiResponseDTO;
import com.gaziyev.microinstaclone.authservice.dto.JwtAuthenticationResponseDTO;
import com.gaziyev.microinstaclone.authservice.dto.UserSummaryDTO;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.repository.UserRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@Order(5)
@DisplayName("User Controller Integration Test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private HttpHeaders headers;
    private String accessToken;
    private String refreshToken;

    @Autowired
    private UserRepository userRepository;
    private String userId;

    @BeforeEach
    void setUp() {

        headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
    }

    @AfterAll
    void afterAll() {

        userRepository.deleteById(userId);
    }

    @Test
    @Order(2)
    @DisplayName("Trying to register user")
    void testCreateUser_givenSignUpRequestDTO_whenValidUserProvided_thenReturnCreatedUser() throws JSONException {

        String username = "konan";
        final String POST_SIGN_UP = "/auth/sign-up";
        final String expectedMessage = "User registered successfully";
        final String errorMessageForActualMessage = "message response should be like: User registered successfully";


        HttpEntity<String> request = new HttpEntity<>(
                getJsonObject(username, username).toString(),
                setContentType(headers)
        );

        ResponseEntity<ApiResponseDTO> response
                = testRestTemplate.postForEntity(POST_SIGN_UP, request, ApiResponseDTO.class);

        ApiResponseDTO apiResponseDTO = response.getBody();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(apiResponseDTO);
        assertEquals(expectedMessage, apiResponseDTO.getMessage(),
                errorMessageForActualMessage
        );
        assertTrue(apiResponseDTO.getSuccess(), "success response should be true");
    }

    @Test
    @Order(3)
    @DisplayName("Trying to register exists user")
    void testCreateUser_givenSignUpRequestDTO_whenExistsUserProvided_thenReturnBadRequest() throws JSONException {

        String username = "konan";
        final String POST_SIGN_UP = "/auth/sign-up";
        final String expectedMessage = String.format("User with username %s already exists", username);
        final String errorMessageForActualMessage = "message response should be like: " + expectedMessage;

        HttpEntity<String> request = new HttpEntity<>(
                getJsonObject(username, username).toString(),
                setContentType(headers)
        );

        ResponseEntity<ApiResponseDTO> response
                = testRestTemplate.postForEntity(POST_SIGN_UP, request, ApiResponseDTO.class);

        ApiResponseDTO apiResponseDTO = response.getBody();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(apiResponseDTO);
        assertNull(apiResponseDTO.getSuccess());
        assertEquals(expectedMessage, apiResponseDTO.getMessage(),
                errorMessageForActualMessage
        );
    }

    @Test
    @Order(1)
    @DisplayName("Trying to accept into protected route without jwt token")
    void testFindAll_whenTokenIsNotProvided_thenReturnUnauthorized() {

        final String POST_SIGN_UP = "/users/all";
        final String errorMessageForActualStatusCode
                = "status response should be like: " + HttpStatus.UNAUTHORIZED;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(
                null,
                headers
        );

        ResponseEntity<ApiResponseDTO> response = testRestTemplate.exchange(
                POST_SIGN_UP,
                HttpMethod.GET,
                request,
                ApiResponseDTO.class
        );

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode(),
                errorMessageForActualStatusCode
        );
    }

    @Test
    @Order(4)
    @DisplayName("Trying to login user")
    void testAuthenticateUser_givenLoginRequestDTO_whenValidUserProvided_thenReturnMapOfTokens() throws JSONException {

        final String POST_SIGN_IN = "/auth/sign-in";
        final String EXPECTED_TOKEN_TYPE = "Bearer";
        final String ACCESS_TOKEN = "access_token";
        final String REFRESH_TOKEN = "refresh_token";
        final String errorMessageForNullableAccessToken = "access token should be provided";
        final String errorMessageForNullableRefreshToken = "refresh token should be provided";

        JSONObject jsonObject = new JSONObject()
                .put("username", "konan")
                .put("password", "123123");

        HttpEntity<String> request = new HttpEntity<>(jsonObject.toString(), setContentType(headers));

        ResponseEntity<JwtAuthenticationResponseDTO> response
                = testRestTemplate.postForEntity(POST_SIGN_IN, request, JwtAuthenticationResponseDTO.class);

        JwtAuthenticationResponseDTO jwtResponse = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getTokens());
        assertNotNull(jwtResponse.getTokens().get(ACCESS_TOKEN),
                errorMessageForNullableAccessToken);
        assertNotNull(jwtResponse.getTokens().get(REFRESH_TOKEN),
                errorMessageForNullableRefreshToken);
        assertEquals(EXPECTED_TOKEN_TYPE, jwtResponse.getTokenType());

        accessToken = jwtResponse.getTokens().get(ACCESS_TOKEN);
        refreshToken = jwtResponse.getTokens().get(REFRESH_TOKEN);
    }

    @Test
    @Order(5)
    @DisplayName("Trying to upload profile picture")
    void testUploadProfilePicture_givenProfilePictureUrl_whenPictureUpdated_thenReturnApiResponseDTO() throws JSONException {

        String profile_picture = "test.jpg";
        final String PUT_UPLOAD_PROFILE_PICTURE_FOR_CURRENT_USER = "/users/me/picture";
        final String expectedMessage = "Profile picture updated successfully";
        final String errorMessageForActualMessage = "message response should be like: " + expectedMessage;
        final String errorMessageForActualBooleanValue = "success response should be true";

        JSONObject jsonObject = new JSONObject()
                .put("profilePicture", profile_picture);

        HttpEntity<String> request = new HttpEntity<>(
                jsonObject.toString(),
                setBearerAuth(
                        setContentType(headers)
                )
        );

        ResponseEntity<ApiResponseDTO> response = testRestTemplate.exchange(
                PUT_UPLOAD_PROFILE_PICTURE_FOR_CURRENT_USER,
                HttpMethod.PUT,
                request,
                ApiResponseDTO.class
        );

        ApiResponseDTO apiResponseDTO = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(apiResponseDTO);
        assertEquals(expectedMessage, apiResponseDTO.getMessage(),
                errorMessageForActualMessage
        );
        assertTrue(apiResponseDTO.getSuccess(), errorMessageForActualBooleanValue);
    }

    @Test
    @Order(6)
    @DisplayName("Trying to obtain user by username")
    void testFindUser_givenUsernameFromPath_whenUserFound_thenReturnUser() {

        String username = "konan";
        final String GET_USER_BY_USERNAME = String.format("/users/summary/%s", username);

        HttpEntity<String> request = new HttpEntity<>(setBearerAuth(headers));

        ResponseEntity<User> response = testRestTemplate.exchange(
                GET_USER_BY_USERNAME,
                HttpMethod.GET,
                request,
                User.class
        );

        User user = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(user);
        assertEquals(username, user.getUsername());

        userId = user.getId();
    }

    @Test
    @Order(7)
    @DisplayName("Trying to get all users")
    void testFindAll_returnAllUsers() {

        final String GET_ALL_USERS = "/users/all";

        HttpEntity<String> request = new HttpEntity<>(setBearerAuth(headers));

        ResponseEntity<List<User>> response = testRestTemplate.exchange(
                GET_ALL_USERS,
                HttpMethod.GET,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        List<User> users = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(users);
    }

    @Test
    @Order(8)
    @DisplayName("Trying to obtain current user summary")
    void testGetCurrentUserSummary_returnCurrentUserSummary() {

        final String username = "konan";
        final String GET_CURRENT_USER_SUMMARY = "/users/me";


        HttpEntity<String> request = new HttpEntity<>(setBearerAuth(headers));

        ResponseEntity<UserSummaryDTO> response = testRestTemplate.exchange(
                GET_CURRENT_USER_SUMMARY,
                HttpMethod.GET,
                request,
                UserSummaryDTO.class
        );

        UserSummaryDTO userSummaryDTO = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(userSummaryDTO);
        assertEquals(username, userSummaryDTO.getUsername());
    }

    @Test
    @Order(9)
    @DisplayName("Trying to obtain user summary by username")
    void testFindUserSummary_givenUsernameFromPath_whenUserFound_thenReturnUserSummary() {

        String username = "konan";
        final String GET_USER_SUMMARY_BY_USERNAME = String.format("/users/summary/%s", username);

        HttpEntity<String> request = new HttpEntity<>(setBearerAuth(headers));

        ResponseEntity<UserSummaryDTO> response = testRestTemplate.exchange(
                GET_USER_SUMMARY_BY_USERNAME,
                HttpMethod.GET,
                request,
                UserSummaryDTO.class
        );

        UserSummaryDTO userSummaryDTO = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(userSummaryDTO);
        assertEquals(username, userSummaryDTO.getUsername());
    }

    @Test
    @Order(10)
    @DisplayName("Trying to obtain user summary by usernames")
    void testGetUserSummaries_givenUsernameList_whenUsernamesFound_thenReturnUserSummaries() throws JSONException {

        final String username = "konan";
        final String POST_USER_SUMMARIES_BY_USERNAMES = "/users/summary/in";

        JSONArray jsonArray = new JSONArray(
                List.of(username)
        );

        HttpEntity<String> request = new HttpEntity<>(
                jsonArray.toString(),
                setBearerAuth(
                        setContentType(headers)
                )
        );

        ResponseEntity<List<UserSummaryDTO>> response = testRestTemplate.exchange(
                POST_USER_SUMMARIES_BY_USERNAMES,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<>() {
                }
        );

        List<UserSummaryDTO> userSummaryDTOS = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(userSummaryDTOS);
    }

    @Test
    @Order(11)
    @DisplayName("Trying to refresh access token")
    void testRefreshToken_givenRefreshToken_whenValidTokenProvided_thenReturnMapOfTokens() throws JSONException {

        final String POST_REFRESH_TOKEN = "/auth/refresh-token";
        final String EXPECTED_TOKEN_TYPE = "Bearer";
        final String ACCESS_TOKEN = "access_token";
        final String REFRESH_TOKEN = "refresh_token";
        final String errorMessageForNullableAccessToken = "access token should be provided";
        final String errorMessageForNullableRefreshToken = "refresh token should be provided";

        headers.setBearerAuth(refreshToken);
        JSONObject jsonObject = new JSONObject()
                .put("refreshToken", refreshToken);


        HttpEntity<String> request = new HttpEntity<>(
                jsonObject.toString(),
                setBearerAuth(
                        setContentType(headers)
                )
        );

        ResponseEntity<JwtAuthenticationResponseDTO> response = testRestTemplate.exchange(
                POST_REFRESH_TOKEN,
                HttpMethod.POST,
                request,
                JwtAuthenticationResponseDTO.class
        );

        JwtAuthenticationResponseDTO jwtResponse = response.getBody();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.getTokens());
        assertNotNull(jwtResponse.getTokens().get(ACCESS_TOKEN),
                errorMessageForNullableAccessToken);
        assertNotNull(jwtResponse.getTokens().get(REFRESH_TOKEN),
                errorMessageForNullableRefreshToken);
        assertEquals(EXPECTED_TOKEN_TYPE, jwtResponse.getTokenType());

        accessToken = jwtResponse.getTokens().get(ACCESS_TOKEN);
        refreshToken = jwtResponse.getTokens().get(REFRESH_TOKEN);
    }

    private JSONObject getJsonObject(String name, String username) throws JSONException {
        return new JSONObject()
                .put("name", name)
                .put("username", username)
                .put("email", username + "@gmail.com")
                .put("password", "123123");
    }

    private HttpHeaders setContentType(HttpHeaders headers) {

        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private HttpHeaders setBearerAuth(HttpHeaders headers) {

        headers.setBearerAuth(accessToken);
        return headers;
    }

}
