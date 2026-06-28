package com.jtech.tasklist.backend.integration;

import com.jtech.tasklist.backend.auth.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    void register_ShouldReturn200_WhenDataIsValid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900000L))
                .andExpect(jsonPath("$.refreshExpiresIn").value(604800000L));
    }

    @Test
    void register_ShouldReturn400_WhenEmailAlreadyExists() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John Doe\",\"email\":\"john@email.com\",\"password\":\"654321\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenNameIsBlank() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenEmailIsBlank() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"\",\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturn200AndTokens_WhenUserAlreadyExists() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900000L))
                .andExpect(jsonPath("$.refreshExpiresIn").value(604800000L));
    }

    @Test
    void login_ShouldReturn401_WhenEmailDoesNotExist() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"unknown@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_ShouldReturn401_WhenPasswordIsWrong() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@email.com\",\"password\":\"wrongpass\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_ShouldReturn400_WhenEmailIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"invalid-email\",\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturn400_WhenEmailIsBlank() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"\",\"password\":\"123456\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_ShouldReturn400_WhenPasswordIsBlank() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"john@email.com\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_ShouldReturn400_WhenAllFieldsAreMissing() throws Exception {
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void refresh_ShouldReturn200_WhenTokenIsValid() throws Exception {
        var registerResult = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"John\",\"email\":\"john@email.com\",\"password\":\"123456\"}"))
                .andExpect(status().isOk())
                .andReturn();

        var content = registerResult.getResponse().getContentAsString();
        var refreshToken = content.split("\"refreshToken\":\"")[1].split("\"")[0];

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900000L))
                .andExpect(jsonPath("$.refreshExpiresIn").value(604800000L));
    }

    @Test
    void refresh_ShouldReturn401_WhenTokenIsInvalid() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"invalid-token\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refresh_ShouldReturn400_WhenTokenIsBlank() throws Exception {
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"\"}"))
                .andExpect(status().isBadRequest());
    }
}
