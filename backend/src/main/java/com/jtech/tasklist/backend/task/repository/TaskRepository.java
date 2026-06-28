package com.jtech.tasklist.backend.task.repository;

import com.jtech.tasklist.backend.task.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByUserIdOrderByCreatedAtDesc(UUID userId);
    Optional<Task> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
    long countByTaskListId(UUID taskListId);
}
