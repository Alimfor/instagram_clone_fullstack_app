package com.gaziyev.microinstaclone.authservice.service;

import com.gaziyev.microinstaclone.authservice.model.InstaUserDetails;
import com.gaziyev.microinstaclone.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(InstaUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
