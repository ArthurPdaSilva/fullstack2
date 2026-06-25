package com.jtech.tasklist.backend.auth;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.dto.LoginRequest;
import com.jtech.tasklist.backend.auth.dto.RegisterRequest;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.auth.service.AuthService;
import com.jtech.tasklist.backend.exception.BadRequestException;
import com.jtech.tasklist.backend.exception.UnauthorizedException;
import com.jtech.tasklist.backend.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private PasswordEncoder passwordEncoder;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        var userId = UUID.randomUUID();
        var user = User.builder()
                .id(userId)
                .name("John")
                .email("john@email.com")
                .password(passwordEncoder.encode("123456"))
                .createdAt(LocalDateTime.now())
                .build();
        var request = new LoginRequest("john@email.com", "123456");

        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));
        when(jwtTokenProvider.generateAccessToken(userId.toString())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(userId.toString())).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpiration()).thenReturn(604800000L);

        var response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getType());
        assertEquals(900000L, response.getExpiresIn());
        assertEquals(604800000L, response.getRefreshExpiresIn());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldThrowException_WhenEmailDoesNotExist() {
        var request = new LoginRequest("unknown@email.com", "123456");

        when(userRepository.findByEmail("unknown@email.com")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsWrong() {
        var user = User.builder()
                .id(UUID.randomUUID())
                .name("John")
                .email("john@email.com")
                .password(passwordEncoder.encode("correct-password"))
                .build();
        var request = new LoginRequest("john@email.com", "wrong-password");

        when(userRepository.findByEmail("john@email.com")).thenReturn(Optional.of(user));

        assertThrows(UnauthorizedException.class, () -> authService.login(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldCreateUserAndReturnAuthResponse_WhenEmailIsNew() {
        var userId = UUID.randomUUID();
        var request = new RegisterRequest("John", "john@email.com", "123456");
        var savedUser = User.builder()
                .id(userId)
                .name("John")
                .email("john@email.com")
                .password(passwordEncoder.encode("123456"))
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.existsByEmail("john@email.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtTokenProvider.generateAccessToken(userId.toString())).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(userId.toString())).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpiration()).thenReturn(900000L);
        when(jwtTokenProvider.getRefreshTokenExpiration()).thenReturn(604800000L);

        var response = authService.register(request);

        assertNotNull(response);
        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getType());
        assertEquals(900000L, response.getExpiresIn());
        assertEquals(604800000L, response.getRefreshExpiresIn());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        var request = new RegisterRequest("John", "john@email.com", "123456");

        when(userRepository.existsByEmail("john@email.com")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }
}
