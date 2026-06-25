package com.jtech.tasklist.backend.integration;

import com.jtech.tasklist.backend.task.repository.TaskRepository;
import com.jtech.tasklist.backend.auth.repository.UserRepository;
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
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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
        userRepository.deleteAll();
    }

    @Test
    void create_ShouldReturn201() throws Exception {
        mockMvc.perform(post("/tasks")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My Task\",\"description\":\"Task description\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("My Task"))
                .andExpect(jsonPath("$.description").value("Task description"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void create_ShouldReturn403_WithoutToken() throws Exception {
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My Task\",\"description\":\"Task description\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void findAll_ShouldReturnTasks() throws Exception {
        mockMvc.perform(post("/tasks")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Task 1\",\"description\":\"Desc 1\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/tasks")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Task 2\",\"description\":\"Desc 2\"}"))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/tasks")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void findById_ShouldReturnTask() throws Exception {
        var result = mockMvc.perform(post("/tasks")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My Task\",\"description\":\"Task description\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var content = result.getResponse().getContentAsString();
        var id = content.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(get("/tasks/{id}", id)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("My Task"));
    }

    @Test
    void findById_ShouldReturn404_WhenNotOwned() throws Exception {
        mockMvc.perform(get("/tasks/{id}", "00000000-0000-0000-0000-000000000000")
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturnUpdatedTask() throws Exception {
        var result = mockMvc.perform(post("/tasks")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My Task\",\"description\":\"Task description\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var content = result.getResponse().getContentAsString();
        var id = content.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(put("/tasks/{id}", id)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Task\",\"description\":\"Updated desc\",\"completed\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.description").value("Updated desc"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    void delete_ShouldReturn204() throws Exception {
        var result = mockMvc.perform(post("/tasks")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"My Task\",\"description\":\"Task description\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        var content = result.getResponse().getContentAsString();
        var id = content.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(delete("/tasks/{id}", id)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }
}
