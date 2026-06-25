package com.jtech.tasklist.backend.task.service;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.exception.ResourceNotFoundException;
import com.jtech.tasklist.backend.exception.UnauthorizedException;
import com.jtech.tasklist.backend.task.domain.Task;
import com.jtech.tasklist.backend.task.dto.TaskRequest;
import com.jtech.tasklist.backend.task.dto.TaskResponse;
import com.jtech.tasklist.backend.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional
    public TaskResponse create(TaskRequest request, String userId) {
        var user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.isCompleted())
                .user(user)
                .build();

        task = taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    @Transactional(readOnly = true)
    public List<TaskResponse> findAllByUserId(String userId) {
        return taskRepository.findByUserIdOrderByCreatedAtDesc(UUID.fromString(userId))
                .stream()
                .map(TaskResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskResponse findByIdAndUserId(UUID taskId, String userId) {
        var task = taskRepository.findByIdAndUserId(taskId, UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse update(UUID taskId, TaskRequest request, String userId) {
        var task = taskRepository.findByIdAndUserId(taskId, UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());

        task = taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void delete(UUID taskId, String userId) {
        var task = taskRepository.findByIdAndUserId(taskId, UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Task not found or access denied"));
        taskRepository.delete(task);
    }
}
