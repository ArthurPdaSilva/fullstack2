package com.jtech.tasklist.backend.tasklist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskListRequest {

    @NotBlank(message = "Name is required")
    private String name;
}
