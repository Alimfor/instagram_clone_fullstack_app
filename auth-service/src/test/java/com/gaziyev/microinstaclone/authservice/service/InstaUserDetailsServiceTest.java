package com.gaziyev.microinstaclone.authservice.service;

import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Order(3)
@DisplayName("Insta User Details Service Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class InstaUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private InstaUserDetailsService underTest;
    private String username;

    @BeforeEach
    void setUp() {
        username = "username";
    }

    @Test
    @Order(1)
    @DisplayName("Trying to obtain user by username")
    void testLoadUserByUsername_givenUsername_whenUsernameIsExists_thenConvertToUserDetailsAndReturn() {

        User user = User.builder()
                .username(username)
                .password("password")
                .email("temp@gmail.com")
                .active(true)
                .userProfile(Profile.builder()
                        .displayName("username")
                        .profilePictureUrl("temp.jpg")
                        .birthday(new Date())
                        .build())
                .roles(new HashSet<>() {
                    {
                        add(Role.USER);
                    }
                })
                .build();
        Set<String> expectedRoles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = underTest.loadUserByUsername(user.getUsername());
        Set<String> actualRoles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .collect(Collectors.toSet());


        verify(userRepository).findByUsername(user.getUsername());
        assertEquals(user.getUsername(), userDetails.getUsername());
        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    @Order(2)
    @DisplayName("Trying to catch UsernameNotFoundException")
    void testLoadUserByUsername_givenUsername_whenUsernameIsNotExists_thenThrowUsernameNotFoundException() {

        String expectedThrownMessage = "User not found";
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.empty());

        UsernameNotFoundException actualThrownException = assertThrows(UsernameNotFoundException.class,
                () -> underTest.loadUserByUsername(username).toString(),
                "Unexpected exception"
        );

        verify(userRepository).findByUsername(username);
        assertEquals(expectedThrownMessage, actualThrownException.getMessage(),
                "Unexpected exception message");
    }
}
