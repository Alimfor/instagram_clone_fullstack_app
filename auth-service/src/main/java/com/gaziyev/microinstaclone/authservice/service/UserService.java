package com.gaziyev.microinstaclone.authservice.service;

import com.gaziyev.microinstaclone.authservice.entity.Role;
import com.gaziyev.microinstaclone.authservice.entity.User;
import com.gaziyev.microinstaclone.authservice.exception.EmailAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.exception.UsernameAlreadyExistsException;
import com.gaziyev.microinstaclone.authservice.messaging.UserEventSender;
import com.gaziyev.microinstaclone.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final UserEventSender userEventSender;

	public List<User> findAll() {
		log.info("retrieving all users");
		return userRepository.findAll();
	}

	public Optional<User> findUserByUsername(String username) {
		log.info("retrieving user by username: {}", username);
		return userRepository.findByUsername(username);
	}

	public List<User> findByUsernameIn(List<String> usernames) {
		return userRepository.findByUsernameIn(usernames);
	}

	@Transactional
	public User registerUser(User user) {
		log.info("registering user: {}", user);

		if (userRepository.existsByUsername(user.getUsername())) {
			log.warn("user with username {} already exists", user.getUsername());
			throw new UsernameAlreadyExistsException(
					String.format("User with username %s already exists", user.getUsername())
			);
		}

		if (userRepository.existsByEmail(user.getEmail())) {
			log.warn("user with email {} already exists", user.getEmail());
			throw new EmailAlreadyExistsException(
					String.format("User with email %s already exists", user.getEmail())
			);
		}

		user.setActive(true);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setRoles(new HashSet<>() {
			{
				add(Role.USER);
			}
		});

		User savedUser = userRepository.save(user);
		userEventSender.sendUserCreated(savedUser);
		return savedUser;
	}

	@Transactional
	public User updateProfilePicture(String uri, String id) {
		log.info("updating profile picture {} for user with id: {}", uri, id);

		return userRepository
				.findById(id)
				.map(user -> {
					String oldProfilePicture = user.getUserProfile().getProfilePictureUrl();
					user.getUserProfile().setProfilePictureUrl(uri);
					User savedUser = userRepository.save(user);

					userEventSender.sendUserUpdated(savedUser, oldProfilePicture);

					return savedUser;
				})
				.orElseThrow(() ->
						             new ResourceNotFoundException(
								             String.format("User with id %s not found", id)
						             ));
	}
}
