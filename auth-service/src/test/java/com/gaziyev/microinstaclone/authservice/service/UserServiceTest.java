package com.gaziyev.microinstaclone.authservice.service;

import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.exception.EmailAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.exception.ResourceNotFoundException;
import com.gaziyev.microinstaclone.authservice.exception.UsernameAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.messaging.UserEventSender;
import com.gaziyev.microinstaclone.authservice.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@Order(2)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("User Service Test")
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEventSender userEventSender;

    @InjectMocks
    private UserService userService;
    private User user;
    private final String otherUserName = "otherUser";

    @BeforeEach
    void setUp() {

        user = User.builder()
                .username("username")
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
    }

    @Test
    @Order(1)
    @DisplayName("Trying to obtain all users")
    void whenFindAll_thenReturnUserList() {

        when(userRepository.findAll()).thenReturn(getUsers());

        userService.findAll();
        verify(userRepository).findAll();
    }


    @Test
    @Order(2)
    @DisplayName("Trying to obtain user by username")
    void whenFindUserByUsername_thenReturnUser() {

        when(userRepository.findByUsername(user.getUsername())).thenReturn(
                Optional.of(user));

        userService.findUserByUsername(user.getUsername());
        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    @Order(3)
    @DisplayName("Trying to obtain user list by usernames")
    void whenFindByUsernameIn_thenReturnUserList() {

        when(userRepository.findByUsernameIn(
                        List.of(user.getUsername(), otherUserName)
                )
        ).thenReturn(getUsers());

        userService.findByUsernameIn(List.of(user.getUsername(), otherUserName));

        verify(userRepository).findByUsernameIn(
                List.of(user.getUsername(), otherUserName)
        );
    }

    @Test
    @Order(4)
    @DisplayName("Trying to register user")
    void givenUser_whenRegisterUser_thenPassSuccess() {

        when(userRepository.existsByUsername(user.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(false);

        when(passwordEncoder.encode(user.getPassword()))
                .thenReturn(user.getPassword());

        when(userRepository.save(user)).thenReturn(user);

        userService.registerUser(user);

        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).save(user);
        verify(userEventSender).sendUserCreated(user);
    }

    @Test
    @Order(5)
    @DisplayName("Trying to catch UsernameAlreadyExistsException")
    void givenUserAndSameUsername_whenToCallExistsByUsername_thenThrowUsernameAlreadyExistsException() {

        when(userRepository.existsByUsername(user.getUsername()))
                .thenReturn(true);

        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.registerUser(user),
                "Unexpected exception"
        );

        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository, never()).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
        verify(userEventSender, never()).sendUserCreated(user);
    }

    @Test
    @Order(6)
    @DisplayName("Trying to catch EmailAlreadyExistsException")
    void givenUserAndSameUsername_whenToCallExistsByEmail_thenThrowEmailAlreadyExistsException() {

        when(userRepository.existsByUsername(user.getUsername()))
                .thenReturn(false);
        when(userRepository.existsByEmail(user.getEmail()))
                .thenReturn(true);

        assertThrows(EmailAlreadyExistsException.class,
                () -> userService.registerUser(user),
                "Unexpected exception"
        );

        verify(userRepository).existsByUsername(user.getUsername());
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository, never()).save(user);
        verify(userEventSender, never()).sendUserCreated(user);
    }

    @Test
    @Order(7)
    @DisplayName("Trying to update profile picture")
    void givenUriAndUserId_whenUpdateProfilePicture_thenPassSuccess() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        when(userRepository.save(user))
                .thenReturn(user);

        String oldProfilePictureUrl = user.getUserProfile().getProfilePictureUrl();

        userService.updateProfilePicture("uri", user.getId());

        verify(userRepository).findById(user.getId());
        verify(userRepository).save(user);
        verify(userEventSender).sendUserUpdated(user, oldProfilePictureUrl);
    }

    @Test
    @Order(8)
    @DisplayName("Trying to catch ResourceNotFoundException if user not fount by id")
    void givenUriAndUserId_whenToCallFindById_thenThrowResourceNotFoundException() {

        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () ->userService.updateProfilePicture("uri", user.getId()),
                "Unexpected exception"
        );

        verify(userRepository).findById(user.getId());
        verify(userRepository, never()).save(user);
        verify(userEventSender, never()).sendUserUpdated(user, null);
    }

    private List<User> getUsers() {

        return List.of(user, User.builder()
                .username(otherUserName)
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
                .build());
    }
}
