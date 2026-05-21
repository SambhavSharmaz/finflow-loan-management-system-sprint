package com.capgemini.authservice.service;

import com.capgemini.authservice.dto.LoginRequest;
import com.capgemini.authservice.dto.SignupRequest;
import com.capgemini.authservice.dto.UserDTO;
import com.capgemini.authservice.entity.Role;
import com.capgemini.authservice.entity.User;
import com.capgemini.authservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_Success() {
        SignupRequest request = new SignupRequest();
        request.setName("John");
        request.setEmail("john@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setName("John");
        savedUser.setEmail("john@test.com");
        savedUser.setPassword("encodedPassword");
        savedUser.setRole(Role.ROLE_USER);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.register(request);

        assertNotNull(result);
        assertEquals("john@test.com", result.getEmail());
        assertEquals("John", result.getName());
        assertEquals(Role.ROLE_USER, result.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_EmailAlreadyExists() {
        SignupRequest request = new SignupRequest();
        request.setEmail("john@test.com");

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_Success() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@test.com");
        request.setPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true);

        User result = authService.login(request);

        assertNotNull(result);
        assertEquals("john@test.com", result.getEmail());
    }

    @Test
    void login_UserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@test.com");
        request.setPassword("password123");

        when(userRepository.findByEmail("unknown@test.com")).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authService.login(request));
    }

    @Test
    void login_InvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("john@test.com");
        request.setPassword("wrongPassword");

        User user = new User();
        user.setEmail("john@test.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.login(request));
    }

    @Test
    void getAllUsers_Success() {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("John");
        user1.setEmail("john@test.com");
        user1.setRole(Role.ROLE_USER);

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Admin");
        user2.setEmail("admin@test.com");
        user2.setRole(Role.ROLE_ADMIN);

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDTO> result = authService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("John", result.get(0).getName());
        assertEquals("ROLE_USER", result.get(0).getRole());
        assertEquals("ROLE_ADMIN", result.get(1).getRole());
    }

    @Test
    void updateUserRole_Success() {
        User user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@test.com");
        user.setRole(Role.ROLE_USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        UserDTO result = authService.updateUserRole(1L, "ROLE_ADMIN");

        assertEquals("ROLE_ADMIN", result.getRole());
        assertEquals("John", result.getName());
    }

    @Test
    void updateUserRole_UserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> authService.updateUserRole(99L, "ROLE_ADMIN"));
    }

    @Test
    void updateUserRole_InvalidRole() {
        User user = new User();
        user.setId(1L);
        user.setRole(Role.ROLE_USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(IllegalArgumentException.class, () -> authService.updateUserRole(1L, "INVALID_ROLE"));
    }
}
