package com.capgemini.authservice.controller;

import com.capgemini.authservice.dto.ApiResponse;
import com.capgemini.authservice.dto.AuthResponse;
import com.capgemini.authservice.dto.LoginRequest;
import com.capgemini.authservice.dto.SignupRequest;
import com.capgemini.authservice.entity.Role;
import com.capgemini.authservice.entity.User;
import com.capgemini.authservice.service.AuthService;
import com.capgemini.authservice.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void signup_Success() {
        SignupRequest request = new SignupRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");
        when(authService.register(any(SignupRequest.class))).thenReturn(user);

        ApiResponse<String> response = authController.signup(request);

        assertTrue(response.isSuccess());
        assertEquals("Signup completed successfully.", response.getMessage());
        verify(authService).register(request);
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@test.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");
        user.setRole(Role.ROLE_USER);

        when(authService.login(any(LoginRequest.class))).thenReturn(user);
        when(jwtUtil.generateToken("john@test.com", Role.ROLE_USER)).thenReturn("test-jwt-token");

        ApiResponse<AuthResponse> response = authController.login(request);

        assertTrue(response.isSuccess());
        assertEquals("Login completed successfully.", response.getMessage());
        assertNotNull(response.getData());
        assertEquals("test-jwt-token", response.getData().getToken());
        assertEquals(Role.ROLE_USER, response.getData().getRole());
    }
}
