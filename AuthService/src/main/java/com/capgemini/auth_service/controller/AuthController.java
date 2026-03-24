package com.capgemini.auth_service.controller;

import com.capgemini.auth_service.dto.AuthResponse;
import com.capgemini.auth_service.dto.LoginRequest;
import com.capgemini.auth_service.dto.SignupRequest;
import com.capgemini.auth_service.entity.User;
import com.capgemini.auth_service.service.AuthService;
import com.capgemini.auth_service.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService service;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {
        service.register(request);
        return "User Registered Successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        User user = service.login(request);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(token, user.getRole());
    }
}