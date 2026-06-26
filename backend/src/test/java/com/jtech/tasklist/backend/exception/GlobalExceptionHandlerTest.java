package com.jtech.tasklist.backend.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    void handleResourceNotFound_ShouldReturn404() {
        var ex = new ResourceNotFoundException("User not found");
        ResponseEntity<ApiError> response = handler.handleResourceNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals("User not found", response.getBody().getMessage());
    }

    @Test
    void handleBadRequest_ShouldReturn400() {
        var ex = new BadRequestException("Invalid data");
        ResponseEntity<ApiError> response = handler.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals("Invalid data", response.getBody().getMessage());
    }

    @Test
    void handleUnauthorized_ShouldReturn401() {
        var ex = new UnauthorizedException("Access denied");
        ResponseEntity<ApiError> response = handler.handleUnauthorized(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals("Access denied", response.getBody().getMessage());
    }

    @Test
    void handleValidationErrors_ShouldReturn400WithFieldErrors() {
        var target = new Object();
        var bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "email", "Email is required"));
        bindingResult.addError(new FieldError("target", "name", "Name is required"));
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response = handler.handleValidationErrors(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals("Validation failed", response.getBody().getMessage());
        assertNotNull(response.getBody().getErrors());
        assertEquals(2, response.getBody().getErrors().size());
        assertTrue(response.getBody().getErrors().contains("Email is required"));
        assertTrue(response.getBody().getErrors().contains("Name is required"));
    }

    @Test
    void handleGeneral_ShouldReturn500() {
        var ex = new RuntimeException("Unexpected error");
        ResponseEntity<ApiError> response = handler.handleGeneral(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, Objects.requireNonNull(response.getBody()).getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }

    @Test
    void handleTypeMismatch_ShouldReturn400() {
        var ex = new MethodArgumentTypeMismatchException("invalid-uuid", java.util.UUID.class, "taskId", null, null);
        ResponseEntity<ApiError> response = handler.handleTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, Objects.requireNonNull(response.getBody()).getStatus());
        assertTrue(response.getBody().getMessage().contains("taskId"));
    }
}
