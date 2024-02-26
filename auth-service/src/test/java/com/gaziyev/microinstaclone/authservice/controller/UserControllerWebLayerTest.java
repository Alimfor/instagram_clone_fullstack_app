package com.gaziyev.microinstaclone.authservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gaziyev.microinstaclone.authservice.dto.ApiResponseDTO;
import com.gaziyev.microinstaclone.authservice.dto.SignUpRequestDTO;
import com.gaziyev.microinstaclone.authservice.dto.UserSummaryDTO;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.service.JwtTokenService;
import com.gaziyev.microinstaclone.authservice.service.UserService;
import com.gaziyev.microinstaclone.authservice.util.UserData;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;



@WebMvcTest(
        controllers = UserController.class,
        excludeAutoConfiguration = {SecurityAutoConfiguration.class}
)
@AutoConfigureDataMongo
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerWebLayerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtTokenService jwtTokenService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserService userService;

    @Test
    @Order(5)
    @DisplayName("Trying to create user")
    void testCreateUser_givenSignUpRequestDTO_whenValidUserProvided_thenReturnCreatedUser() throws Exception {

        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setName("alimzhan");
        signUpRequestDTO.setUsername("alim");
        signUpRequestDTO.setEmail("alim@gmail.com");
        signUpRequestDTO.setPassword("123123");

        doNothing().when(userService).registerUser(any(User.class));


        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpRequestDTO));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        ApiResponseDTO createdUser
                = new ObjectMapper().readValue(responseBodyAsString, ApiResponseDTO.class);

        assertEquals("User registered successfully", createdUser.getMessage(),
                "message response should be like: User registered successfully"
        );
        assertTrue(createdUser.getSuccess(), "success response should be true");
    }

    @Test
    @Order(6)
    @DisplayName("Trying to obtain user by username")
    void testFindUser_givenUsernameFromPath_whenUsernameIsExists_thenReturnFoundUser() throws Exception {
        String username = "alimzhan";
        User user = UserData.getUsers(null, username).get(0);

        when(userService.findUserByUsername(any(String.class)))
                .thenReturn(Optional.of(user));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/" + username)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @Order(7)
    @DisplayName("Trying to catch ResourceNotFountException from findUser")
    void testFindUser_givenStrangeUsernameFromPath_whenUsernameIsNotExists_thenThrowResourceNotFountException() throws Exception {

        String username = "alex";

        when(userService.findUserByUsername(username))
                .thenReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(String.format("User with username %s not found", username))
                );
    }

    @Test
    @Order(8)
    @DisplayName("Trying to obtain user summary")
    void testGetUserSummary_givenUsernameFromPath_whenUsernameIsExists_thenReturnUserSummary() throws Exception {

        String username = "alimzhan";
        User user = UserData.getUsers(null, username).get(0);

        when(userService.findUserByUsername(any(String.class)))
                .thenReturn(Optional.of(user));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/summary/{username}", username)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        UserSummaryDTO userSummaryDTO = new ObjectMapper()
                .readValue(responseBodyAsString, UserSummaryDTO.class);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));

        assertEquals(username, userSummaryDTO.getUsername());
    }

    @Test
    @Order(9)
    @DisplayName("Trying to catch ResourceNotFountException from getUserSummary")
    void testGetUserSummary_givenStrangeUsernameFromPath_whenUsernameIsNotExists_thenThrowResourceNotFountException() throws Exception {

        String username = "alex";

        when(userService.findUserByUsername(username))
                .thenReturn(Optional.empty());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/summary/{username}", username)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message")
                        .value(String.format("User with username %s not found", username))
                );
    }

    @Test
    @Order(10)
    @DisplayName("Trying to obtain user summaries")
    void testGetUserSummaries_givenUsernames_whenUsernameIsExists_thenReturnUserSummaries() throws Exception {

        String username = "alimzhan";

        when(userService.findByUsernameIn(anyList()))
                .thenReturn(UserData.getUsers(null, username));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users/summary/in")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(List.of(username)));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        List<UserSummaryDTO> userSummaryDTOs = new ObjectMapper()
                .readValue(responseBodyAsString, new TypeReference<>() {
                });

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON));

        assertEquals(username, userSummaryDTOs.get(0).getUsername());
    }

    @Test
    @Order(1)
    @DisplayName("Trying to validate SignUpRequestDTO to not blank requirements")
    void testCreateUser_givenSignUpRequestDTO_whenDataAreBlank_thenReturnBadRequest() throws Exception {

        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setName(null);
        signUpRequestDTO.setUsername(null);
        signUpRequestDTO.setEmail(null);
        signUpRequestDTO.setPassword(null);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpRequestDTO));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",
                        Is.is("must not be blank")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username",
                        Is.is("must not be blank")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email",
                        Is.is("must not be blank")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password",
                        Is.is("must not be blank")));
    }

    @Test
    @Order(2)
    @DisplayName("Trying to validate SignUpRequestDTO to minimum requirements")
    void testCreateUser_givenSignUpRequestDTO_whenDataHasLessThanMinimumOfRequired_thenReturnBadRequest() throws Exception {

        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setName("ali");
        signUpRequestDTO.setUsername("al");
        signUpRequestDTO.setEmail("test@test.com");
        signUpRequestDTO.setPassword("12345");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpRequestDTO));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",
                        Is.is("size must be between 4 and 40")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username",
                        Is.is("size must be between 3 and 15")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password",
                        Is.is("size must be between 6 and 20")));
    }

    @Test
    @Order(3)
    @DisplayName("Trying to validate SignUpRequestDTO to maximum requirements")
    void testCreateUser_givenSignUpRequestDTO_whenDataHasMoreThanMaximumOfRequired_thenReturnBadRequest() throws Exception {

        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa(44)");
        signUpRequestDTO.setUsername("aaaaaaaaaaaaaaaa(20)");
        signUpRequestDTO.setEmail("testtttttttttttttttttttttttttttt43@test.com");
        signUpRequestDTO.setPassword("1234543243242342323434(26)");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpRequestDTO));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name",
                        Is.is("size must be between 4 and 40")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.username",
                        Is.is("size must be between 3 and 15")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email",
                        Is.is("size must be between 0 and 40")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.password",
                        Is.is("size must be between 6 and 20")));
    }

    @Test
    @Order(4)
    @DisplayName("Trying to validate SignUpRequestDTO to email requirements")
    void testCreateUser_givenSignUpRequestDTO_whenEmailIsNotValid_thenReturnBadRequest() throws Exception {

        SignUpRequestDTO signUpRequestDTO = new SignUpRequestDTO();
        signUpRequestDTO.setName("alimzhan");
        signUpRequestDTO.setUsername("alim");
        signUpRequestDTO.setEmail("alim@test.com()");
        signUpRequestDTO.setPassword("123123");

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/auth/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpRequestDTO));

        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.email",
                        Is.is("must be a well-formed email address")));
    }
}
