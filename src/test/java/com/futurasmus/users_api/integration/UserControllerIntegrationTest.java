package com.futurasmus.users_api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserPatchDto;
import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    // CREATE
    @Test
    void shouldCreateUserSuccessfully() throws Exception {
        // Arrange
        RequestUserDto dto = new RequestUserDto("test@example.com", "Test", "User", "password123");

        // Action / Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email", is("test@example.com")))
            .andExpect(jsonPath("$.firstName", is("Test")))
            .andExpect(jsonPath("$.lastName", is("User")));

        assertTrue(userJpaRepository.findByEmail("test@example.com").isPresent());
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "dup@example.com", "Test1", "User1", "password1", null, null, true, false));
        RequestUserDto dto = new RequestUserDto("dup@example.com", "Test2", "User2", "password2");

        // Action / Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnBadRequestWhenNullEmail() throws Exception {
        // Arrange
        RequestUserDto dto = new RequestUserDto(null, "Test", "User", "password123");

        // Action / Assert
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest());
    }

    // READ
    @Test
    void shouldReturnListOfUsers() throws Exception {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "a@example.com", "Test1", "User1", "password", null, null, true, false));
        userJpaRepository.save(new UserEntity(null, "b@example.com", "Test2", "User2", "password", null, null, true, false));

        // Action / Assert
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(2)))
            .andExpect(jsonPath("$.content[0].email", containsString("@example.com")));
    }
    
    @Test
    void shouldReturnEmptyListOfFilteredUsers() throws Exception {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "a@example.com", "Test1", "User1", "password", null, null, true, false));
        userJpaRepository.save(new UserEntity(null, "b@example.com", "Test2", "User2", "password", null, null, true, false));

        // Action / Assert
        mockMvc.perform(get("/api/users?createdBefore=2025-11-10T18:10:00"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(0)));
    }
    
    @Test
    void shouldReturnPageOfOneUser() throws Exception {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "a@example.com", "Test1", "User1", "password", null, null, true, false));
        userJpaRepository.save(new UserEntity(null, "b@example.com", "Test2", "User2", "password", null, null, true, false));

        // Action / Assert
        mockMvc.perform(get("/api/users?page=0&size=1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", hasSize(1)));
    }

    @Test
    void shouldReturnUserById() throws Exception {
        // Arrange
        UserEntity user = userJpaRepository.save(
            new UserEntity(null, "test@example.com", "Test", "User", "password", null, null, true, false)
        );

        // Action / Assert
        mockMvc.perform(get("/api/users/" + user.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email", is("test@example.com")));
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFound() throws Exception {
        // Action / Assert
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenUserIdNotNumber() throws Exception {
        // Action / Assert
        mockMvc.perform(get("/api/users/asd"))
            .andExpect(status().isBadRequest());
    }

    // UPDATE
    @Test
    void shouldUpdateUserSuccessfully() throws Exception {
        // Arrange
        UserEntity user = userJpaRepository.save(
            new UserEntity(null, "update@example.com", "Old", "Name", "password", null, null, true, false)
        );

        RequestUserDto updateDto = new RequestUserDto("update@example.com", "New", "Name", "password");

        // Action / Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("New")));
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFoundOnUpdate() throws Exception {
        // Action / Assert
        mockMvc.perform(put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RequestUserDto("update@example.com", "New", "Name", "password"))))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "dup@example.com", "Test1", "User1", "password1", null, null, true, false));
        UserEntity user = userJpaRepository.save(
            new UserEntity(null, "update@example.com", "Old", "Name", "password", null, null, true, false)
        );

        RequestUserDto updateDto = new RequestUserDto("DUP@example.com", "New", "Name", "password");

        // Action / Assert
        mockMvc.perform(put("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isConflict());
    }

    @Test
    void shouldPatchUserSuccessfully() throws Exception {
        // Arrange
        UserEntity user = userJpaRepository.save(
            new UserEntity(null, "patch@example.com", "Old", "Name", "password", null, null, true, false)
        );

        // Action / Assert
        mockMvc.perform(patch("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RequestUserPatchDto(null, "Patched", null, null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("Patched")));
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFoundOnPatch() throws Exception {
        // Action / Assert
        mockMvc.perform(patch("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new RequestUserPatchDto(null, "Patched", null, null))))
            .andExpect(status().isNotFound());
    }

    @Test
    void patchShouldReturnConflictWhenEmailAlreadyExists() throws Exception {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "dup@example.com", "Test1", "User1", "password1", null, null, true, false));
        UserEntity user = userJpaRepository.save(
            new UserEntity(null, "patch@example.com", "Old", "Name", "password", null, null, true, false)
        );

        RequestUserPatchDto patchDto = new RequestUserPatchDto("DUP@example.com", null, null, null);

        // Action / Assert
        mockMvc.perform(patch("/api/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(patchDto)))
            .andExpect(status().isConflict());
    }

    // DELETE
    @Test
    void shouldDeleteUserSuccessfully() throws Exception {
        // Arrange
        UserEntity user = userJpaRepository.save(
                new UserEntity(null, "delete@example.com", "To", "Delete", "password", null, null, true, false)
        );

        // Action / Assert
        mockMvc.perform(delete("/api/users/" + user.getId()))
                .andExpect(status().isNoContent());

        Optional<UserEntity> deleted = userJpaRepository.findByEmail("delete@example.com");
        assertTrue(deleted.isEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenUserNotFoundOnDelete() throws Exception {
        // Action / Assert
        mockMvc.perform(delete("/api/users/999"))
            .andExpect(status().isNotFound());
    }
}
