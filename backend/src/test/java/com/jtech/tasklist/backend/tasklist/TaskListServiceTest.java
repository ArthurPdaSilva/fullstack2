package com.jtech.tasklist.backend.tasklist;

import com.jtech.tasklist.backend.auth.domain.User;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.exception.AccessDeniedException;
import com.jtech.tasklist.backend.exception.ResourceNotFoundException;
import com.jtech.tasklist.backend.task.repository.TaskRepository;
import com.jtech.tasklist.backend.tasklist.domain.TaskList;
import com.jtech.tasklist.backend.tasklist.dto.TaskListRequest;
import com.jtech.tasklist.backend.tasklist.repository.TaskListRepository;
import com.jtech.tasklist.backend.tasklist.service.TaskListService;
import com.jtech.tasklist.backend.tasklist.service.TaskListServiceImpl;
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
class TaskListServiceTest {

    @Mock
    private TaskListRepository taskListRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    private TaskListService taskListService;

    private UUID userId;
    private UUID taskListId;
    private User user;
    private TaskList taskList;

    @BeforeEach
    void setUp() {
        taskListService = new TaskListServiceImpl(taskListRepository, userRepository, taskRepository);

        userId = UUID.randomUUID();
        taskListId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .name("John")
                .email("john@email.com")
                .build();

        taskList = TaskList.builder()
                .id(taskListId)
                .name("Trabalho")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void create_ShouldReturnTaskListResponse() {
        var request = new TaskListRequest("Trabalho");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskListRepository.save(any(TaskList.class))).thenReturn(taskList);

        var response = taskListService.create(request, userId.toString());

        assertNotNull(response);
        assertEquals("Trabalho", response.getName());
        assertEquals(taskListId, response.getId());
        assertEquals(0, response.getTaskCount());
        verify(taskListRepository).save(any(TaskList.class));
    }

    @Test
    void create_ShouldTrimName() {
        var request = new TaskListRequest("  Trabalho  ");
        var trimmedList = TaskList.builder()
                .id(taskListId)
                .name("Trabalho")
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(taskListRepository.save(argThat(tl -> tl.getName().equals("Trabalho")))).thenReturn(trimmedList);

        var response = taskListService.create(request, userId.toString());

        assertEquals("Trabalho", response.getName());
    }

    @Test
    void create_ShouldThrowException_WhenUserNotFound() {
        var request = new TaskListRequest("Trabalho");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> taskListService.create(request, userId.toString()));
        verify(taskListRepository, never()).save(any(TaskList.class));
    }

    @Test
    void findAllByUserId_ShouldReturnTaskLists() {
        when(taskListRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of(taskList));
        when(taskRepository.countByTaskListId(taskListId)).thenReturn(3L);

        var taskLists = taskListService.findAllByUserId(userId.toString());

        assertEquals(1, taskLists.size());
        assertEquals("Trabalho", taskLists.get(0).getName());
        assertEquals(3, taskLists.get(0).getTaskCount());
    }

    @Test
    void findAllByUserId_ShouldReturnEmptyList_WhenNoLists() {
        when(taskListRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of());

        var taskLists = taskListService.findAllByUserId(userId.toString());

        assertTrue(taskLists.isEmpty());
    }

    @Test
    void update_ShouldReturnUpdatedTaskList() {
        var request = new TaskListRequest("Updated Name");

        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));
        when(taskListRepository.save(any(TaskList.class))).thenReturn(taskList);
        when(taskRepository.countByTaskListId(taskListId)).thenReturn(5L);

        var response = taskListService.update(taskListId, request, userId.toString());

        assertEquals("Updated Name", response.getName());
        assertEquals(5, response.getTaskCount());
        verify(taskListRepository).save(any(TaskList.class));
    }

    @Test
    void update_ShouldThrowException_WhenTaskListNotFound() {
        var request = new TaskListRequest("Updated Name");

        when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskListService.update(taskListId, request, userId.toString()));
        verify(taskListRepository, never()).save(any(TaskList.class));
    }

    @Test
    void update_ShouldThrowAccessDenied_WhenTaskListBelongsToAnotherUser() {
        var otherUserId = UUID.randomUUID();
        var request = new TaskListRequest("Updated Name");

        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        assertThrows(AccessDeniedException.class,
                () -> taskListService.update(taskListId, request, otherUserId.toString()));
        verify(taskListRepository, never()).save(any(TaskList.class));
    }

    @Test
    void delete_ShouldRemoveTaskList_WhenOwnershipValid() {
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        taskListService.delete(taskListId, userId.toString());

        verify(taskListRepository).delete(taskList);
    }

    @Test
    void delete_ShouldThrowException_WhenTaskListNotFound() {
        when(taskListRepository.findById(taskListId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> taskListService.delete(taskListId, userId.toString()));
        verify(taskListRepository, never()).delete(any(TaskList.class));
    }

    @Test
    void delete_ShouldThrowAccessDenied_WhenTaskListBelongsToAnotherUser() {
        var otherUserId = UUID.randomUUID();

        when(taskListRepository.findById(taskListId)).thenReturn(Optional.of(taskList));

        assertThrows(AccessDeniedException.class,
                () -> taskListService.delete(taskListId, otherUserId.toString()));
        verify(taskListRepository, never()).delete(any(TaskList.class));
    }
}
