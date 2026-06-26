package com.jtech.tasklist.backend.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = Base64.getEncoder().encodeToString("test-secret-key-for-jwt-token-provider-tests-256".getBytes());
        jwtTokenProvider = new JwtTokenProvider(secret, 900000L, 604800000L);
    }

    @Test
    void generateAccessToken_ShouldReturnValidToken() {
        String token = jwtTokenProvider.generateAccessToken("user-123");
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateRefreshToken_ShouldReturnValidToken() {
        String token = jwtTokenProvider.generateRefreshToken("user-123");
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void getUserIdFromToken_ShouldReturnCorrectUserId() {
        String token = jwtTokenProvider.generateAccessToken("user-456");
        String userId = jwtTokenProvider.getUserIdFromToken(token);
        assertEquals("user-456", userId);
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsInvalid() {
        assertFalse(jwtTokenProvider.validateToken("invalid-token"));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsEmpty() {
        assertFalse(jwtTokenProvider.validateToken(""));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenTokenIsNull() {
        assertFalse(jwtTokenProvider.validateToken(null));
    }

    @Test
    void getAccessTokenExpiration_ShouldReturnConfiguredValue() {
        assertEquals(900000L, jwtTokenProvider.getAccessTokenExpiration());
    }

    @Test
    void getRefreshTokenExpiration_ShouldReturnConfiguredValue() {
        assertEquals(604800000L, jwtTokenProvider.getRefreshTokenExpiration());
    }

    @Test
    void generateToken_ShouldCreateUniqueTokens_ForDifferentUsers() {
        String token1 = jwtTokenProvider.generateAccessToken("user-1");
        String token2 = jwtTokenProvider.generateAccessToken("user-2");
        assertNotEquals(token1, token2);
    }

    @Test
    void getUserIdFromToken_ShouldThrowException_WhenTokenIsInvalid() {
        assertThrows(Exception.class, () -> jwtTokenProvider.getUserIdFromToken("invalid-token"));
    }
}
