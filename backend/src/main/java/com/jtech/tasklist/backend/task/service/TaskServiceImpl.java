package com.jtech.tasklist.backend.task.service;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.exception.AccessDeniedException;
import com.jtech.tasklist.backend.exception.ResourceNotFoundException;
import com.jtech.tasklist.backend.exception.UnauthorizedException;
import com.jtech.tasklist.backend.task.domain.Task;
import com.jtech.tasklist.backend.task.dto.TaskRequest;
import com.jtech.tasklist.backend.task.dto.TaskResponse;
import com.jtech.tasklist.backend.task.repository.TaskRepository;
import com.jtech.tasklist.backend.tasklist.repository.TaskListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskListRepository taskListRepository;

    @Transactional
    public TaskResponse create(TaskRequest request, String userId) {
        var user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var taskBuilder = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(request.isCompleted())
                .user(user);

        if (request.getTaskListId() != null) {
            var taskList = taskListRepository.findById(request.getTaskListId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task list not found"));

            if (!taskList.getUser().getId().equals(UUID.fromString(userId))) {
                throw new AccessDeniedException("You do not have permission to use this task list");
            }

            taskBuilder.taskList(taskList);
        }

        var task = taskBuilder.build();
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
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(UUID.fromString(userId))) {
            throw new AccessDeniedException("You do not have permission to access this task");
        }

        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public TaskResponse update(UUID taskId, TaskRequest request, String userId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(UUID.fromString(userId))) {
            throw new AccessDeniedException("You do not have permission to access this task");
        }

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());

        task = taskRepository.save(task);
        return TaskResponse.fromEntity(task);
    }

    @Transactional
    public void delete(UUID taskId, String userId) {
        var task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        if (!task.getUser().getId().equals(UUID.fromString(userId))) {
            throw new AccessDeniedException("You do not have permission to access this task");
        }

        taskRepository.delete(task);
    }
}
