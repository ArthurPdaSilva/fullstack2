package com.jtech.tasklist.backend.integration;

import com.jtech.tasklist.backend.auth.repository.UserRepository;
import com.jtech.tasklist.backend.task.repository.TaskRepository;
import com.jtech.tasklist.backend.tasklist.repository.TaskListRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskListRepository taskListRepository;

    @Autowired
    private TaskRepository taskRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());

        var result = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();

        var content = result.getResponse().getContentAsString();
        token = "Bearer " + content.split("\"token\":\"")[1].split("\"")[0];
    }

    @AfterEach
    void cleanup() {
        taskRepository.deleteAll();
        taskListRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/tasklists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Trabalho\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Trabalho"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.taskCount").value(0));
    }

    @Test
    void create_ShouldReturn403_WithoutToken() throws Exception {
        mockMvc.perform(post("/tasklists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Trabalho\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_ShouldReturn400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/tasklists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void findAll_ShouldReturnTaskLists() throws Exception {
        mockMvc.perform(post("/tasklists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Trabalho\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasklists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Estudos\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasklists")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findAll_ShouldReturnEmpty_WhenNoLists() throws Exception {
        mockMvc.perform(get("/tasklists")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void update_ShouldReturnUpdatedTaskList() throws Exception {
        var result = mockMvc.perform(post("/tasklists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Trabalho\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var content = result.getResponse().getContentAsString();
        var id = content.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(put("/tasklists/{id}", id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.taskCount").value(0));
    }

    @Test
    void update_ShouldReturn404_WhenTaskListDoesNotExist() throws Exception {
        mockMvc.perform(put("/tasklists/{id}", "00000000-0000-0000-0000-000000000000")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Name\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        var result = mockMvc.perform(post("/tasklists")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Trabalho\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var content = result.getResponse().getContentAsString();
        var id = content.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(delete("/tasklists/{id}", id)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_ShouldReturn404_WhenTaskListDoesNotExist() throws Exception {
        mockMvc.perform(delete("/tasklists/{id}", "00000000-0000-0000-0000-000000000000")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }
}
