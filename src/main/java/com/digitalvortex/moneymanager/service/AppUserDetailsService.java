package com.digitalvortex.moneymanager.service;

import com.digitalvortex.moneymanager.model.Profile;
import com.digitalvortex.moneymanager.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load user by email: {}", email);

        Profile profile = profileRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("User not found with email: {}", email);
                    return new UsernameNotFoundException("User with email " + email + " not found");
                });

        log.debug("User found: {}", profile.getEmail());
        log.debug("user password: {}", profile.getPassword());

        // Add at least one authority (role)
        return User.builder()
                .username(profile.getEmail())
                .password(profile.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}