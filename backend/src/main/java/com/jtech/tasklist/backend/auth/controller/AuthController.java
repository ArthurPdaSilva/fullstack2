package com.jtech.tasklist.backend.auth.controller;

import com.jtech.tasklist.backend.auth.dto.AuthResponse;
import com.jtech.tasklist.backend.auth.dto.LoginRequest;
import com.jtech.tasklist.backend.auth.dto.RefreshRequest;
import com.jtech.tasklist.backend.auth.dto.RegisterRequest;
import com.jtech.tasklist.backend.auth.service.AuthService;
import com.jtech.tasklist.backend.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with name, email and password")
    @ApiResponse(responseCode = "200", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Validation error or email already in use",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        var response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates with email and password, returns JWT tokens")
    @ApiResponse(responseCode = "200", description = "Authenticated successfully")
    @ApiResponse(responseCode = "401", description = "Invalid email or password",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        var response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token", description = "Exchanges a valid refresh token for new access and refresh tokens")
    @ApiResponse(responseCode = "200", description = "Token refreshed successfully")
    @ApiResponse(responseCode = "400", description = "Refresh token is blank",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "401", description = "Refresh token expired or invalid",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        var response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }
}
