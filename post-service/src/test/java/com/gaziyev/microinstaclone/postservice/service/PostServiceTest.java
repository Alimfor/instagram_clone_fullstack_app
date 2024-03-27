package com.gaziyev.microinstaclone.postservice.service;

import com.gaziyev.microinstaclone.postservice.dto.PostRequestDTO;
import com.gaziyev.microinstaclone.postservice.entity.Post;
import com.gaziyev.microinstaclone.postservice.exception.NotAllowedException;
import com.gaziyev.microinstaclone.postservice.exception.ResourceNotFoundException;
import com.gaziyev.microinstaclone.postservice.messaging.PostEventSender;
import com.gaziyev.microinstaclone.postservice.repository.PostRepository;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Order(2)
@DisplayName("Post Service Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostRepository repository;

    @Mock
    private PostEventSender postEventSender;

    @InjectMocks
    private PostService underTest;

    private Post expected;
    private static final String USERNAME = "some-username";
    private List<String> ids;

    @BeforeEach
    void setUp() {
        final String IMAGE_URL = "http://example.com/images/test-image.jpg";

        expected = Instancio.of(Post.class)
                .set(field(Post::getUsername), USERNAME)
                .set(field(Post::getLastModifiedBy), USERNAME)
                .set(field(Post::getImageUrl), IMAGE_URL)
                .create();
    }

    @Test
    @Order(1)
    @DisplayName("Trying to create post")
    void testCreatePost_givenPostRequestDTO_whenSaved_thenReturnSavedPost() {

        PostRequestDTO postRequest = Instancio.of(PostRequestDTO.class)
                .set(field(PostRequestDTO::getImageUrl), "http://example.com/images/test-image.jpg")
                .create();

        when(repository.save(any(Post.class)))
                .thenReturn(expected);
        doNothing().when(postEventSender)
                .sendPostCreated(any(Post.class));

        Post actual = underTest.createPost(postRequest);

        assertNotNull(actual);
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getImageUrl(), actual.getImageUrl());
        assertEquals(expected.getCaption(), actual.getCaption());
    }

    @Test
    @Order(2)
    @DisplayName("Trying to delete post")
    void testDeletePost_givenPostIdAndUsername_whenDeleted_thenReturnTrue() {

        final String ERROR_MSG = "deletePost should not throw an exception";

        when(repository.findById(anyString()))
                .thenReturn(Optional.of(expected));
        doNothing().when(repository)
                .delete(any(Post.class));
        doNothing().when(postEventSender)
                .sendPostDeleted(any(Post.class));

        assertDoesNotThrow(
                () -> underTest.deletePost(expected.getId(), USERNAME),
                ERROR_MSG
        );
        verify(repository).findById(anyString());
        verify(repository).delete(any(Post.class));
        verify(postEventSender).sendPostDeleted(any(Post.class));
    }

    @Test
    @Order(3)
    @DisplayName("Trying to catch NotAllowedException")
    void testDeletePost_givenPostIdAndUsername_whenUsernameIsNotAllowed_thenThrowNotAllowedException() {

        final String ERROR_MSG = "deletePost should throw the NotAllowedException";
        final String OTHER_USERNAME = "other-username";

        expected.setUsername(OTHER_USERNAME);
        expected.setLastModifiedBy(OTHER_USERNAME);

        when(repository.findById(anyString()))
                .thenReturn(Optional.of(expected));

        assertThrows(NotAllowedException.class,
                () -> underTest.deletePost(expected.getId(), USERNAME),
                ERROR_MSG
        );

        assertionsDeletePostThrownException();
    }

    @Test
    @Order(4)
    @DisplayName("Trying to catch ResourceNotFoundException")
    void testDeletePost_givenPostIdAndUsername_whenPostIsNotFound_thenResourceNotFoundException() {

        final String ERROR_MSG = "deletePost should throw the ResourceNotFoundException";

        when(repository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> underTest.deletePost(expected.getId(), USERNAME),
                ERROR_MSG
        );

        assertionsDeletePostThrownException();
    }

    @Test
    @Order(5)
    @DisplayName("Trying to obtain posts by username")
    void testPostsByUsername_givenUsername_whenFound_thenReturnPosts() {

        when(repository.findByUsernameOrderByCreatedAtDesc(anyString()))
                .thenReturn(List.of(expected));

        List<Post> posts = underTest.postsByUsername(USERNAME);

        assertionsPostByUsernameAndPostsByIdIn(posts);
    }

    @Test
    @Order(6)
    @DisplayName("Trying to obtain posts by ids")
    void testPostsByIdIn_givenIds_whenFound_thenReturnPosts() {

        when(repository.findByIdInOrderByCreatedAtDesc(anyList()))
                .thenReturn(List.of(expected));

        List<Post> posts = underTest.postsByIdIn(List.of(expected.getId()));

        assertionsPostByUsernameAndPostsByIdIn(posts);
    }

    private void assertionsDeletePostThrownException() {

        verify(repository).findById(anyString());
        verify(repository, never()).delete(any(Post.class));
        verify(postEventSender, never()).sendPostDeleted(any(Post.class));
    }

    private void assertionsPostByUsernameAndPostsByIdIn(List<Post> posts) {

        final int EXPECTED_LIST_SIZE = 1;

        assertNotNull(posts);
        assertEquals(EXPECTED_LIST_SIZE, posts.size());
        assertEquals(expected.getUsername(), posts.get(0).getUsername());
        assertEquals(expected.getImageUrl(), posts.get(0).getImageUrl());
        assertEquals(expected.getCaption(), posts.get(0).getCaption());
    }
}
