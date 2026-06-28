package com.jtech.tasklist.backend.tasklist.service;

import com.jtech.tasklist.backend.tasklist.dto.TaskListRequest;
import com.jtech.tasklist.backend.tasklist.dto.TaskListResponse;

import java.util.List;
import java.util.UUID;

public interface TaskListService {

    TaskListResponse create(TaskListRequest request, String userId);

    List<TaskListResponse> findAllByUserId(String userId);

    TaskListResponse update(UUID id, TaskListRequest request, String userId);

    void delete(UUID id, String userId);
}
