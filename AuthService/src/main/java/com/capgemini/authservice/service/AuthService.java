package com.capgemini.authservice.service;

import com.capgemini.authservice.dto.LoginRequest;
import com.capgemini.authservice.dto.SignupRequest;
import com.capgemini.authservice.dto.UserDTO;
import com.capgemini.authservice.entity.Role;
import com.capgemini.authservice.entity.User;
import com.capgemini.authservice.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NoSuchElementException("User not found."));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password.");
        }
        return user;
    }

    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole().name()));
        }
        return dtos;
    }

    public UserDTO updateUserRole(Long id, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found."));

        try {
            user.setRole(Role.valueOf(role));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role. Use ROLE_USER or ROLE_ADMIN.");
        }

        User savedUser = userRepository.save(user);
        return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getEmail(), savedUser.getRole().name());
    }
}
