package com.jtech.tasklist.backend.task.controller;

import com.jtech.tasklist.backend.exception.ApiError;
import com.jtech.tasklist.backend.task.dto.TaskRequest;
import com.jtech.tasklist.backend.task.dto.TaskResponse;
import com.jtech.tasklist.backend.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a task", description = "Creates a new task for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Validation error",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TaskResponse> create(@Valid @RequestBody TaskRequest request,
                                               Authentication authentication) {
        var response = taskService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all tasks", description = "Returns all tasks belonging to the authenticated user")
    @ApiResponse(responseCode = "200", description = "List of tasks returned")
    public ResponseEntity<List<TaskResponse>> findAll(Authentication authentication) {
        var tasks = taskService.findAllByUserId(authentication.getName());
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID", description = "Returns a task by its ID if it belongs to the authenticated user")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "400", description = "Invalid UUID format",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Access denied – task belongs to another user",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Task not found",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TaskResponse> findById(@PathVariable UUID id,
                                                  Authentication authentication) {
        var response = taskService.findByIdAndUserId(id, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task", description = "Updates an existing task if it belongs to the authenticated user")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "400", description = "Validation error or invalid UUID",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Access denied – task belongs to another user",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Task not found",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TaskResponse> update(@PathVariable UUID id,
                                                @Valid @RequestBody TaskRequest request,
                                                Authentication authentication) {
        var response = taskService.update(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task", description = "Deletes a task if it belongs to the authenticated user")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "400", description = "Invalid UUID format",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Access denied – task belongs to another user",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Task not found",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       Authentication authentication) {
        taskService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
