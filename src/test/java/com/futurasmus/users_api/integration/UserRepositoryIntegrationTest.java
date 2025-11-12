package com.futurasmus.users_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.domain.Specification;

import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.infrastructure.repository.UserJpaRepository;

import jakarta.validation.ConstraintViolationException;

@DataJpaTest
public class UserRepositoryIntegrationTest {
     @Autowired
    private UserJpaRepository userJpaRepository;

    @Test
    void shouldSaveAndFindUser() {
        // Arrange
        UserEntity user = new UserEntity(null, "test@example.com", "Test", "User", "password", null, null, true, false);

        // Action
        userJpaRepository.save(user);
        Optional<UserEntity> result = userJpaRepository.findByEmail("test@example.com");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(user.getEmail(), result.get().getEmail());
        assertEquals(user.getFirstName(), result.get().getFirstName());
        assertEquals(user.getLastName(), result.get().getLastName());
        assertEquals(user.getPassword(), result.get().getPassword());
        assertTrue(result.get().getId() != null);
        assertTrue(result.get().getCreatedAt() != null);
        assertTrue(result.get().getUpdatedAt() != null);
    }

    @Test
    void shouldFilterUsersUsingSpecification() {
        // Arrange
        UserEntity user1 = new UserEntity(null, "test1@example.com", "Test1", "User1", "password1", null, null, true, false);
        UserEntity user2 = new UserEntity(null, "test2@example.com", "Test2", "User2", "password2", null, null, true, false);
        UserEntity user3 = new UserEntity(null, "test3@example.com", "Test3", "User3", "password3", null, null, false, true);

        userJpaRepository.saveAll(List.of(user1, user2, user3));

        Specification<UserEntity> spec = (root, query, cb) -> cb.and(
                cb.isTrue(root.get("active")),
                cb.like(root.get("email"), "%example%")
        );

        // Action
        List<UserEntity> result = userJpaRepository.findAll(spec);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("test1@example.com")));
        assertTrue(result.stream().anyMatch(u -> u.getEmail().equals("test2@example.com")));
        assertFalse(result.stream().anyMatch(u -> u.getEmail().equals("test3@example.com")));
    }

    @Test
    void shouldFailWhenDuplicateEmail() {
        // Arrange
        UserEntity user1 = new UserEntity(null, "test1@example.com", "Test1", "User1", "password1", null, null, true, false);
        UserEntity user2 = new UserEntity(null, "test1@example.com", "Test2", "User2", "password2", null, null, true, false);

        userJpaRepository.save(user1);

        // Action Assert
        assertThrows(
            DataIntegrityViolationException.class,
            () -> {userJpaRepository.saveAndFlush(user2);}
        );
    }

    @Test
    void shouldFailWhenNullEmail(){
        // Arrange
        UserEntity user = new UserEntity(null, null, "Test", "User", "password", null, null, true, false);

        // Action Assert
        assertThrows(
            ConstraintViolationException.class,
            () -> {userJpaRepository.saveAndFlush(user);}
        );
    }
    
    @Test
    void shouldFailWhenShortPassword(){
        // Arrange
        UserEntity user = new UserEntity(null, null, "Test", "User", "pass", null, null, true, false);

        // Action Assert
        assertThrows(
            ConstraintViolationException.class,
            () -> {userJpaRepository.saveAndFlush(user);}
        );
    }
    
    @Test
    void shouldFailWhenNullActive(){
        // Arrange
        UserEntity user = new UserEntity(null, null, "Test", "User", "pass", null, null, null, false);

        // Action Assert
        assertThrows(
            ConstraintViolationException.class,
            () -> {userJpaRepository.saveAndFlush(user);}
        );
    }
    
    @Test
    void shouldFailWhenNullVerified(){
        // Arrange
        UserEntity user = new UserEntity(null, null, "Test", "User", "pass", null, null, true, null);

        // Action Assert
        assertThrows(
            ConstraintViolationException.class,
            () -> {userJpaRepository.saveAndFlush(user);}
        );
    }
    
}
