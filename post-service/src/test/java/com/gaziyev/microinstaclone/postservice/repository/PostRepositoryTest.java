package com.gaziyev.microinstaclone.postservice.repository;

import com.gaziyev.microinstaclone.postservice.entity.Post;
import org.instancio.Instancio;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;

@Order(1)
@DisplayName("Post Repository Tests")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PostRepositoryTest {

    @Autowired
    private PostRepository underTest;

    private static final String USERNAME = "some-username";
    private List<String> ids;

    @BeforeAll
    void beforeAll() {

        List<Post> posts = Instancio.ofList(Post.class)
                .size(9)
                .set(field(Post::getUsername), USERNAME)
                .set(field(Post::getLastModifiedBy), USERNAME)
                .set(field(Post::getImageUrl), "http://example.com/images/test-image.jpg")
                .ignore(field(Post::getId))
                .ignore(field(Post::getCreatedAt))
                .ignore(field(Post::getUpdatedAt))
                .create();

        ids = underTest.saveAll(posts).stream()
                .map(Post::getId)
                .limit(4)
                .toList();
    }

    @AfterAll
    void afterAll() {

        underTest.deleteAll();
    }

    @Test
    @Order(1)
    @DisplayName("Trying to save post")
    void testSave_givenPost_whenSaved_thenReturnSavedPost() {

        final String POST_NULL_ERROR_MSG = "savedPost should not be null";
        final String POST_ID_ERROR_MSG = "savedPost's id should not be null";

        Post post = Instancio.of(Post.class)
                .set(field(Post::getUsername), USERNAME)
                .set(field(Post::getLastModifiedBy), USERNAME)
                .set(field(Post::getImageUrl), "http://example.com/images/test-image.jpg")
                .ignore(field(Post::getId))
                .ignore(field(Post::getCreatedAt))
                .ignore(field(Post::getUpdatedAt))
                .create();

        Post savedPost = underTest.save(post);

        assertNotNull(savedPost, POST_NULL_ERROR_MSG);
        assertNotNull(savedPost.getId(), POST_ID_ERROR_MSG);
    }

    @Test
    @Order(2)
    @DisplayName("Trying to obtain all posts by username")
    void testFindByUsernameOrderByCreatedAtDesc_givenUsername_whenFound_thenReturnPosts() {

        List<Post> posts = underTest.findByUsernameOrderByCreatedAtDesc(USERNAME);

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
    }

    @Test
    @Order(3)
    @DisplayName("Trying to obtain posts by ids")
    void testFindByIdInOrderByCreatedAtDesc_givenIds_whenFound_thenReturnPosts() {

        List<Post> posts = underTest.findByIdInOrderByCreatedAtDesc(ids);

        assertNotNull(posts);
        assertFalse(posts.isEmpty());
    }
}
