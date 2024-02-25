package com.gaziyev.microinstaclone.authservice.repository;

import com.gaziyev.microinstaclone.authservice.entity.Profile;
import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
	private String user_id;

	@Test
	@Order(1)
	@DisplayName("Trying to save user object")
	void givenUserObject_whenSave_thenReturnSavedUser() {

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
		user_id  = savedUser.getId();

		assertThat(savedUser).isNotNull();
		assertThat(savedUser.getId()).isNotEmpty();
	}

	@Test
	@Order(2)
	@DisplayName("Trying to obtain user by username")
	void givenUsername_whenFindByUsername_thenReturnUser() {

		Optional<User> foundUser = repository.findByUsername(username);

		assertThat(foundUser).isPresent();
		assertThat(foundUser.get().getUsername()).isEqualTo(username);
	}

	@Test
	@Order(3)
	@DisplayName("Make sure that the user with the given username exists")
	void givenUsername_whenExistsByUsername_thenReturnTrue() {

		boolean exists = repository.existsByUsername(username);

		assertThat(exists).isTrue();
	}

	@Test
	@Order(4)
	@DisplayName("Make sure that the user with the given email exists")
	void givenEmail_whenExistsByEmail_thenReturnTrue() {

		boolean exists = repository.existsByEmail(email);

		assertThat(exists).isTrue();
	}

	@Test
	@Order(5)
	@DisplayName("Trying to obtain user list by usernames")
	void givenUsernames_whenFindByUsernameIn_thenReturnUserList() {

		List<User> foundUsers = repository.findByUsernameIn(List.of(username));

		assertThat(foundUsers).isNotEmpty();
		assertThat(foundUsers.get(0).getUsername()).isEqualTo(username);
	}

	@Test
	@Order(6)
	@DisplayName("Trying to update user")
	void givenUser_whenUpdate_thenReturnUpdatedUser() {

		final String newEmail = "new@gmail.com";
		Optional<User> foundUser = repository.findByUsername(username);

		foundUser.get().setEmail(newEmail);
		User updatedUser = repository.save(foundUser.get());

		assertThat(updatedUser.getEmail()).isEqualTo(newEmail);
		assertThat(updatedUser.getUsername()).isEqualTo(username);
	}

	@Test
	@Order(7)
	@DisplayName("Trying to delete user")
	void givenUserId_whenDeleteById_thenReturnNothing() {

		repository.deleteById(user_id);
		boolean isExists = repository.existsById(user_id);

		assertThat(isExists).isFalse();
	}
}
