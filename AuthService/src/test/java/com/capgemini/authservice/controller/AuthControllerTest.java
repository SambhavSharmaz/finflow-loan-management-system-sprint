package com.capgemini.authservice.controller;

import com.capgemini.authservice.dto.AuthResponse;
import com.capgemini.authservice.dto.LoginRequest;
import com.capgemini.authservice.dto.SignupRequest;
import com.capgemini.authservice.entity.Role;
import com.capgemini.authservice.entity.User;
import com.capgemini.authservice.service.AuthService;
import com.capgemini.authservice.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@WebMvcTest(controllers = AuthController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class, properties = {"spring.cloud.config.enabled=false", "spring.config.import="})
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignup_Success() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        signupRequest.setName("Test User");
        Mockito.doNothing().when(authService).register(any(SignupRequest.class));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Signup completed successfully."))
                .andExpect(jsonPath("$.data").value("User registered successfully."));
    }

    @Test
    public void testLogin_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(Role.ROLE_USER);

        Mockito.when(authService.login(any(LoginRequest.class))).thenReturn(user);
        Mockito.when(jwtUtil.generateToken(anyString(), any(Role.class))).thenReturn("mockJwtToken");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login completed successfully."))
                .andExpect(jsonPath("$.data.token").value("mockJwtToken"))
                .andExpect(jsonPath("$.data.role").value("ROLE_USER"));
    }
}
