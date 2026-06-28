package com.jtech.tasklist.backend.tasklist.controller;

import com.jtech.tasklist.backend.tasklist.dto.TaskListRequest;
import com.jtech.tasklist.backend.tasklist.dto.TaskListResponse;
import com.jtech.tasklist.backend.tasklist.service.TaskListService;
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
    public ResponseEntity<TaskListResponse> create(@Valid @RequestBody TaskListRequest request,
                                                   Authentication authentication) {
        var response = taskListService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<TaskListResponse>> findAll(Authentication authentication) {
        var taskLists = taskListService.findAllByUserId(authentication.getName());
        return ResponseEntity.ok(taskLists);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskListResponse> update(@PathVariable UUID id,
                                                   @Valid @RequestBody TaskListRequest request,
                                                   Authentication authentication) {
        var response = taskListService.update(id, request, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id,
                                       Authentication authentication) {
        taskListService.delete(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
