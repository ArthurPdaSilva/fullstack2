package com.jtech.tasklist.backend.auth.service;

import com.jtech.tasklist.backend.auth.dto.AuthResponse;
import com.jtech.tasklist.backend.auth.dto.LoginRequest;
import com.jtech.tasklist.backend.auth.dto.RefreshRequest;
import com.jtech.tasklist.backend.auth.dto.RegisterRequest;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse register(RegisterRequest request);

    AuthResponse refresh(RefreshRequest request);
}
