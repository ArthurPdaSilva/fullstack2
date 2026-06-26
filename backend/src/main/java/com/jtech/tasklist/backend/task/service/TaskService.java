package com.jtech.tasklist.backend.task.service;

import com.jtech.tasklist.backend.task.dto.TaskRequest;
import com.jtech.tasklist.backend.task.dto.TaskResponse;

import java.util.List;
import java.util.UUID;

public interface TaskService {

    TaskResponse create(TaskRequest request, String userId);

    List<TaskResponse> findAllByUserId(String userId);

    TaskResponse findByIdAndUserId(UUID taskId, String userId);

    TaskResponse update(UUID taskId, TaskRequest request, String userId);

    void delete(UUID taskId, String userId);
}
