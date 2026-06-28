package com.jtech.tasklist.backend.task;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.exception.AccessDeniedException;
import com.jtech.tasklist.backend.exception.ResourceNotFoundException;
import com.jtech.tasklist.backend.task.domain.Task;
import com.jtech.tasklist.backend.task.dto.TaskRequest;
import com.jtech.tasklist.backend.task.repository.TaskRepository;
import com.jtech.tasklist.backend.task.service.TaskService;
import com.jtech.tasklist.backend.task.service.TaskServiceImpl;
import com.jtech.tasklist.backend.tasklist.repository.TaskListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskListRepository taskListRepository;

    private TaskService taskService;

    private UUID userId;
    private UUID taskId;
    private User user;
    private Task task;

    @BeforeEach
    void setUp() {
        taskService = new TaskServiceImpl(taskRepository, userRepository, taskListRepository);

        userId = UUID.randomUUID();
        taskId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("John")
                .email("john@email.com")
                .build();

        task = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .completed(false)
                .user(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_ShouldReturnTaskResponse() {
        var request = new TaskRequest("Test Task", "Test Description", false, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        var response = taskService.create(request, userId.toString());

        assertNotNull(response);
        assertEquals("Test Task", response.getTitle());
        assertEquals("Test Description", response.getDescription());
        assertFalse(response.isCompleted());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        var request = new TaskRequest("Test Task", "Test Description", false, null);
 
         when(userRepository.findById(userId)).thenReturn(Optional.empty());
 
         assertThrows(ResourceNotFoundException.class, () -> taskService.create(request, userId.toString()));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void findAllByUserId_ShouldReturnTasks() {
        when(taskRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(task));

        var tasks = taskService.findAllByUserId(userId.toString());

        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.get(0).getTitle());
    }

    @Test
    void findByIdAndUserId_ShouldReturnTask_WhenOwnershipValid() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        var response = taskService.findByIdAndUserId(taskId, userId.toString());

        assertNotNull(response);
        assertEquals(taskId, response.getId());
    }

    @Test
    void findByIdAndUserId_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.findByIdAndUserId(taskId, userId.toString()));
    }

    @Test
    void findByIdAndUserId_ShouldThrowAccessDenied_WhenTaskBelongsToAnotherUser() {
        var otherUserId = UUID.randomUUID();
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class,
                () -> taskService.findByIdAndUserId(taskId, otherUserId.toString()));
    }

    @Test
    void update_ShouldReturnUpdatedTask() {
        var updateRequest = new TaskRequest("Updated Title", "Updated Description", true, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        var response = taskService.update(taskId, updateRequest, userId.toString());

        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertTrue(response.isCompleted());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void update_ShouldThrowException_WhenTaskNotFound() {
        var updateRequest = new TaskRequest("Updated Title", "Updated Description", true, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.update(taskId, updateRequest, userId.toString()));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void update_ShouldThrowAccessDenied_WhenTaskBelongsToAnotherUser() {
        var otherUserId = UUID.randomUUID();
        var updateRequest = new TaskRequest("Updated Title", "Updated Description", true, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class,
                () -> taskService.update(taskId, updateRequest, otherUserId.toString()));
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void delete_ShouldRemoveTask_WhenOwnershipValid() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.delete(taskId, userId.toString());

        verify(taskRepository).delete(task);
    }

    @Test
    void delete_ShouldThrowException_WhenTaskNotFound() {
        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskService.delete(taskId, userId.toString()));
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void delete_ShouldThrowAccessDenied_WhenTaskBelongsToAnotherUser() {
        var otherUserId = UUID.randomUUID();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        assertThrows(AccessDeniedException.class,
                () -> taskService.delete(taskId, otherUserId.toString()));
        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void create_ShouldAcceptNullDescription() {
        var request = new TaskRequest("Title Only", null, false, null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        var response = taskService.create(request, userId.toString());

        assertNotNull(response);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void findAllByUserId_ShouldReturnEmptyList_WhenNoTasks() {
        when(taskRepository.findByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of());

        var tasks = taskService.findAllByUserId(userId.toString());

        assertTrue(tasks.isEmpty());
    }

    @Test
    void update_ShouldPreserveCompletedFlag_WhenNotChanged() {
        task.setCompleted(true);
        var updateRequest = new TaskRequest("Updated Title", "Updated Description", true, null);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        var response = taskService.update(taskId, updateRequest, userId.toString());

        assertTrue(response.isCompleted());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void update_ShouldUnmarkCompleted_WhenSetToFalse() {
        task.setCompleted(true);
        var updateRequest = new TaskRequest("Updated Title", "Updated Description", false, null);
 
         when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        var response = taskService.update(taskId, updateRequest, userId.toString());

        assertFalse(response.isCompleted());
        verify(taskRepository).save(any(Task.class));
    }
}
