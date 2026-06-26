package com.jtech.tasklist.backend.jwt;

public interface TokenProvider {

    String generateAccessToken(String userId);

    String generateRefreshToken(String userId);

    String getUserIdFromToken(String token);

    boolean validateToken(String token);

    long getAccessTokenExpiration();

    long getRefreshTokenExpiration();
}
