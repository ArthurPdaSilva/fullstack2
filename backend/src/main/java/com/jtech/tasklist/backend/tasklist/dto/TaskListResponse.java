package com.jtech.tasklist.backend.tasklist.dto;

import com.jtech.tasklist.backend.tasklist.domain.TaskList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListResponse {
    private UUID id;
    private String name;
    private LocalDateTime createdAt;
    private long taskCount;

    public static TaskListResponse fromEntity(TaskList taskList, long taskCount) {
        return TaskListResponse.builder()
                .id(taskList.getId())
                .name(taskList.getName())
                .createdAt(taskList.getCreatedAt())
                .taskCount(taskCount)
                .build();
    }
}
