package com.skillswap.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillswap.model.User;
import com.skillswap.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username,
                         String password,
                         String name,
                         String email,
                         String displayName,
                         String contact,
                         String bio) {

        if (username == null || username.isBlank()) {
            throw new RuntimeException("username is required");
        }
        if (password == null || password.isBlank()) {
            throw new RuntimeException("password is required");
        }
        if (name == null || name.isBlank()) {
            throw new RuntimeException("name is required");
        }
        if (email == null || email.isBlank()) {
            throw new RuntimeException("email is required");
        }

        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        String hash = passwordEncoder.encode(password);
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(hash);
        u.setName(name);
        u.setEmail(email);
        u.setDisplayName(displayName);
        u.setContact(contact);
        u.setBio(bio != null ? bio : "");
        u.setCreatedAt(LocalDateTime.now());
        return userRepository.save(u);
    }

    public User login(String username, String password) {
        if (username == null || password == null) return null;
        return userRepository.findByUsername(username)
                .filter(u -> passwordEncoder.matches(password, u.getPasswordHash()))
                .orElse(null);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User updateProfile(String username, String name, String displayName, String contact, String bio, String profileImage) {
        User u = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        if (name != null) u.setName(name);
        if (displayName != null) u.setDisplayName(displayName);
        if (contact != null) u.setContact(contact);
        if (bio != null) u.setBio(bio);
        if (profileImage != null) u.setProfileImage(profileImage);
        return userRepository.save(u);
    }
}
