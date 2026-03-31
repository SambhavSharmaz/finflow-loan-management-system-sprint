package com.capgemini.authservice.controller;

import com.capgemini.authservice.dto.ApiResponse;
import com.capgemini.authservice.dto.AuthResponse;
import com.capgemini.authservice.dto.LoginRequest;
import com.capgemini.authservice.dto.SignupRequest;
import com.capgemini.authservice.entity.User;
import com.capgemini.authservice.service.AuthService;
import com.capgemini.authservice.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ApiResponse<String> signup(@Valid @RequestBody SignupRequest request) {
        System.out.println("Received signup request for email: " + request.getEmail());
        authService.register(request);
        System.out.println("Signup completed successfully for email: " + request.getEmail());
        return new ApiResponse<>(true, "Signup completed successfully.", "User registered successfully.");
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        System.out.println("Received login request for email: " + request.getEmail());
        User user = authService.login(request);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        AuthResponse authResponse = new AuthResponse(token, user.getRole());
        System.out.println("Login completed successfully for email: " + request.getEmail());
        return new ApiResponse<>(true, "Login completed successfully.", authResponse);
    }
}
