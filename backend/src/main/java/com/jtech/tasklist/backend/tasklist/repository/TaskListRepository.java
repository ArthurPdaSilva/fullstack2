package com.jtech.tasklist.backend.tasklist.repository;

import com.jtech.tasklist.backend.tasklist.domain.TaskList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskListRepository extends JpaRepository<TaskList, UUID> {
    List<TaskList> findByUserIdOrderByCreatedAtAsc(UUID userId);
    Optional<TaskList> findByIdAndUserId(UUID id, UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
