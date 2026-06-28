package com.jtech.tasklist.backend.auth.service;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.dto.AuthResponse;
import com.jtech.tasklist.backend.auth.dto.LoginRequest;
import com.jtech.tasklist.backend.auth.dto.RefreshRequest;
import com.jtech.tasklist.backend.auth.dto.RegisterRequest;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.exception.BadRequestException;
import com.jtech.tasklist.backend.exception.ResourceNotFoundException;
import com.jtech.tasklist.backend.exception.UnauthorizedException;
import com.jtech.tasklist.backend.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        var user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user = userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        String userId = tokenProvider.getUserIdFromToken(refreshToken);

        var user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String userId = user.getId().toString();
        String accessToken = tokenProvider.generateAccessToken(userId);
        String refreshToken = tokenProvider.generateRefreshToken(userId);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .type("Bearer")
                .expiresIn(tokenProvider.getAccessTokenExpiration())
                .refreshExpiresIn(tokenProvider.getRefreshTokenExpiration())
                .build();
    }
}
