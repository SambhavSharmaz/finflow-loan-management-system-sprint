package com.capgemini.auth_service.service;

import com.capgemini.auth_service.dto.LoginRequest;
import com.capgemini.auth_service.dto.SignupRequest;
import com.capgemini.auth_service.entity.Role;
import com.capgemini.auth_service.entity.User;
import com.capgemini.auth_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository repo;

    @Autowired
    private PasswordEncoder encoder;

    public User register(SignupRequest request) {
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(encoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        return repo.save(user);
    }

    public User login(LoginRequest request) {
        User user = repo.findByEmail(request.getEmail()).orElseThrow(() -> new RuntimeException("User not found"));
        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        return user;
    }
}