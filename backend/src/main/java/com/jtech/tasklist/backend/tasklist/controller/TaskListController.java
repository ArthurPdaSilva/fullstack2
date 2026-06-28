package com.jtech.tasklist.backend.tasklist.controller;

import com.jtech.tasklist.backend.exception.ApiError;
import com.jtech.tasklist.backend.tasklist.dto.TaskListRequest;
import com.jtech.tasklist.backend.tasklist.dto.TaskListResponse;
import com.jtech.tasklist.backend.tasklist.service.TaskListService;
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
@RequestMapping("/tasklists")
@RequiredArgsConstructor
public class TaskListController {

    private final TaskListService taskListService;

    @PostMapping
    @Operation(summary = "Create a task list", description = "Creates a new task list for the authenticated user")
    @ApiResponse(responseCode = "201", description = "Task list created")
    @ApiResponse(responseCode = "400", description = "Validation error – name must not be blank",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TaskListResponse> create(@Valid @RequestBody TaskListRequest request,
                                                    Authentication authentication) {
        var response = taskListService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "List all task lists", description = "Returns all task lists belonging to the authenticated user, each with its task count")
    @ApiResponse(responseCode = "200", description = "List of task lists returned")
    public ResponseEntity<List<TaskListResponse>> findAll(Authentication authentication) {
        var taskLists = taskListService.findAllByUserId(authentication.getName());
        return ResponseEntity.ok(taskLists);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Rename a task list", description = "Renames an existing task list if it belongs to the authenticated user")
    @ApiResponse(responseCode = "200", description = "Task list renamed")
    @ApiResponse(responseCode = "400", description = "Validation error or invalid UUID",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Access denied – task list belongs to another user",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Task list not found",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<TaskListResponse> update(@PathVariable UUID id,
                                                    @Valid @RequestBody TaskListRequest request,
                                                    Authentication authentication) {
        var response = taskListService.update(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task list", description = "Deletes a task list and all its tasks if it belongs to the authenticated user")
    @ApiResponse(responseCode = "204", description = "Task list deleted")
    @ApiResponse(responseCode = "400", description = "Invalid UUID format",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "403", description = "Access denied – task list belongs to another user",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Task list not found",
                 content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                        Authentication authentication) {
        taskListService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
