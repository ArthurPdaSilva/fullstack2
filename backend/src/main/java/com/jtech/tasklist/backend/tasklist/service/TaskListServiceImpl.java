package com.jtech.tasklist.backend.tasklist.service;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.exception.AccessDeniedException;
import com.jtech.tasklist.backend.exception.ResourceNotFoundException;
import com.jtech.tasklist.backend.task.repository.TaskRepository;
import com.jtech.tasklist.backend.tasklist.domain.TaskList;
import com.jtech.tasklist.backend.tasklist.dto.TaskListRequest;
import com.jtech.tasklist.backend.tasklist.dto.TaskListResponse;
import com.jtech.tasklist.backend.tasklist.repository.TaskListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskListServiceImpl implements TaskListService {

    private final TaskListRepository taskListRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Transactional
    public TaskListResponse create(TaskListRequest request, String userId) {
        var user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var taskList = TaskList.builder()
                .name(request.getName().trim())
                .user(user)
                .build();

        taskList = taskListRepository.save(taskList);
        return TaskListResponse.fromEntity(taskList, 0);
    }

    @Transactional(readOnly = true)
    public List<TaskListResponse> findAllByUserId(String userId) {
        return taskListRepository.findByUserIdOrderByCreatedAtAsc(UUID.fromString(userId))
                .stream()
                .map(tl -> TaskListResponse.fromEntity(tl, taskRepository.countByTaskListId(tl.getId())))
                .toList();
    }

    @Transactional
    public TaskListResponse update(UUID id, TaskListRequest request, String userId) {
        var taskList = taskListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task list not found"));

        if (!taskList.getUser().getId().equals(UUID.fromString(userId))) {
            throw new AccessDeniedException("You do not have permission to modify this task list");
        }

        taskList.setName(request.getName().trim());
        taskList = taskListRepository.save(taskList);

        return TaskListResponse.fromEntity(taskList, taskRepository.countByTaskListId(taskList.getId()));
    }

    @Transactional
    public void delete(UUID id, String userId) {
        var taskList = taskListRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task list not found"));

        if (!taskList.getUser().getId().equals(UUID.fromString(userId))) {
            throw new AccessDeniedException("You do not have permission to delete this task list");
        }

        taskListRepository.delete(taskList);
    }
}
