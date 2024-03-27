package com.gaziyev.microinstaclone.authservice.repository;

import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

@Order(1)
@DisplayName("User Repository Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DataMongoTest
class UserRepositoryTest {

	@Autowired
	private UserRepository repository;
	private String username;
	private String email;

	@AfterAll
	void afterAll() {

		repository.deleteAll();
	}

	@Test
	@Order(1)
	@DisplayName("Trying to save user object")
	void testSave_givenUser_whenUserSaved_thenReturnSavedUser() {

		User user = User.builder()
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

		User savedUser = repository.save(user);
		username = savedUser.getUsername();
		email    = savedUser.getEmail();

		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isNotEmpty();
	}

	@Test
	@Order(2)
	@DisplayName("Trying to obtain user by username")
	void testFindByUsername_givenUsername_whenUsernameIsExists_thenReturnFoundUserInsideOptional() {

		Optional<User> foundUser = repository.findByUsername(username);

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getUsername()).isEqualTo(username);
	}

	@Test
	@Order(3)
	@DisplayName("Make sure that the user with the given username is not exists")
	void testExistsByUsername_givenUsername_whenUsernameIsNotExists_thenReturnFalse() {

		boolean exists = repository.existsByUsername(username + "shouldFalse");

		assertThat(exists).isFalse();
	}

	@Test
	@Order(4)
	@DisplayName("Make sure that the user with the given username exists")
	void testExistsByUsername_givenUsername_whenUsernameIsExists_thenReturnTrue() {

		boolean exists = repository.existsByUsername(username);

		assertThat(exists).isTrue();
	}

	@Test
	@Order(5)
	@DisplayName("Make sure that the user with the given email exists")
	void testExistsByEmail_givenEmail_whenEmailIsExists_thenReturnTrue() {

		boolean exists = repository.existsByEmail(email);

		assertThat(exists).isTrue();
	}

	@Test
	@Order(6)
	@DisplayName("Make sure that the user with the given email is not exists")
	void testExistsByEmail_givenEmail_whenEmailIsNotExists_thenReturnFalse() {

		boolean exists = repository.existsByEmail( "shouldFalse" + email);

		assertThat(exists).isFalse();
	}

	@Test
	@Order(7)
	@DisplayName("Trying to obtain user list by usernames")
	void testFindByUsernameIn_givenUsernameList_whenUsernamesAreExists_thenReturnUserList() {

		List<User> foundUsers = repository.findByUsernameIn(List.of(username));

		assertThat(foundUsers).isNotEmpty();
		assertThat(foundUsers.get(0).getUsername()).isEqualTo(username);
	}

	@Test
	@Order(8)
	@DisplayName("Trying to update user")
	void testSave_givenUsername_whenUpdatedFoundUserAndSaved_thenReturnUpdatedUser() {

		final String newEmail = "new@gmail.com";
		Optional<User> foundUser = repository.findByUsername(username);

		foundUser.get().setEmail(newEmail);
		User updatedUser = repository.save(foundUser.get());

		assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
		assertThat(updatedUser.getUsername()).isEqualTo(username);
	}
}
