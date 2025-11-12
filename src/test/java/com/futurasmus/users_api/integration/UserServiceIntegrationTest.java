package com.futurasmus.users_api.integration;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.application.service.UserService;
import com.futurasmus.users_api.common.exception.EmailAlreadyExistsException;
import com.futurasmus.users_api.common.exception.UserNotFoundException;
import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.infrastructure.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.deleteAll();
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        RequestUserDto dto = new RequestUserDto("Test@example.com", "Test", "User", "password123");

        // Action
        ResponseUserDto created = userService.createUser(dto);

        // Assert
        assertNotNull(created.id());
        assertEquals(dto.email().toLowerCase(), created.email());
        assertTrue(created.active());
        assertFalse(created.verified());

        UserEntity saved = userJpaRepository.findByEmail("test@example.com").orElseThrow();
        assertEquals("Test", saved.getFirstName());
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // Arrange
        userJpaRepository.save(new UserEntity(null, "dup@example.com", "Test1", "User1", "password1", null, null, true, false));
        RequestUserDto dto = new RequestUserDto("dup@example.com", "Test2", "User2", "password2");

        // Action / Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(dto));
    }

    @Test
    void shouldFindUserById() {
        // Arrange
        UserEntity user = userJpaRepository.save(new UserEntity(null, "findme@example.com", "Find", "Me", "password", null, null, true, false));

        // Action
        ResponseUserDto found = userService.getUserById(user.getId());

        // Assert
        assertEquals(user.getEmail(), found.email());
        assertEquals(user.getFirstName(), found.firstName());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void shouldReturnPageOfAllUsers() {
        // Arrange
        userJpaRepository.saveAll(List.of(
                new UserEntity(null, "test1@example.com", "Test1", "User1", "password1", null, null, true, false),
                new UserEntity(null, "test2@example.com", "Test2", "User2", "password2", null, null, true, false)
        ));
        RequestUserFilterDto filters = new RequestUserFilterDto(null, null, null, null, null, null, null, null);

        // Action
        Page<ResponseUserDto> users = userService.getAllUsers(filters, Pageable.unpaged());

        // Assert
        assertEquals(2, users.getTotalElements());
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Arrange
        UserEntity existing = userJpaRepository.save(new UserEntity(null, "updatable@example.com", "Old", "Name", "password", null, null, true, false));
        RequestUserDto dto = new RequestUserDto("updated@example.com", "New", "Name", "password");

        // Action
        ResponseUserDto updated = userService.updateUser(existing.getId(), dto);

        // Assert
        assertEquals("updated@example.com", updated.email());
        assertEquals("New", updated.firstName());
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        // Arrange
        UserEntity user = userJpaRepository.save(new UserEntity(null, "delete@example.com", "Del", "User", "password", null, null, true, false));

        // Action
        userService.deleteUser(user.getId());

        // Assert
        assertFalse(userJpaRepository.findById(user.getId()).isPresent());
    }
}
