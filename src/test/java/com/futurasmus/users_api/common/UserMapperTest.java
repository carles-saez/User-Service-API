package com.futurasmus.users_api.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserPatchDto;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.domain.model.User;
import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.util.TestUserMapper;

public class UserMapperTest {

    final TestUserMapper mapper = Mappers.getMapper(TestUserMapper.class);

    User user;
    UserEntity userEntity;
    RequestUserDto requestUserDto;
    ResponseUserDto responseUserDto;
    RequestUserPatchDto requestUserPatchDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "test@example.com", "Test", "User", "password", null, null, true, false);
        userEntity = new UserEntity(1L, "test@example.com", "Test", "User", "password", null, null, true, false);
        requestUserDto = new RequestUserDto("test@example.com", "Test", "User", "password");
        responseUserDto = new ResponseUserDto(1L, "test@example.com", "Test", "User", null, null, true, false);
        requestUserPatchDto = new RequestUserPatchDto("test@example.com", "Test", "User", "password");
    }
    
    @Test
    void toDomain_withValidUserEntity_shouldReturnUser() {
        // Arrange
        // Action
        User result = mapper.toDomain(userEntity);
        // Assert
        assertEquals(user, result);
    }

    @Test
    void toEntity_withValidUser_shouldReturnUserEntity() {
        // Arrange
        // Action
        UserEntity result = mapper.toEntity(user);
        //Assert
        assertEquals(userEntity, result);
    }

    @Test
    void toResponse_withValidUser_shouldReturnResponseUserDto() {
        // Arrange
        // Action
        ResponseUserDto result = mapper.toResponse(user);
        // Assert
        assertEquals(responseUserDto, result);
    }

    @Test
    void updateUserFromDto_withValidUserDto_shouldUpdateUser() {
        // Arrange
        User userToUpdate = new User(1L, "testUpdate@example.com", "TestUpdate", "UserUpdate", "passwordUpdate", null, null, true, false);
        // Action
        mapper.updateUserFromDto(requestUserDto, userToUpdate);
        // Assert
        assertEquals(user, userToUpdate);
    }

    @Test
    void patchUserFromDto_withValidUserPatchDto_shouldUpdateUser() {
        // Arrange
        User userToPatch = new User(1L, "testPatch@example.com", "TestPatch", "UserPatch", "passwordPatch", null, null, true, false);
        // Action
        mapper.patchUserFromDto(requestUserPatchDto, userToPatch);
        // Assert
        assertEquals(user, userToPatch);
    }

    @Test
    void patchUserFromDto_withNullValuesUserPatchDto_shouldUpdateUser() {
        // Arrange
        RequestUserPatchDto requestUserPatchDtoNullEmail = new RequestUserPatchDto(null, "Test", "User", null);
        User userToPatch = new User(1L, "testPatch@example.com", "TestPatch", "UserPatch", "passwordPatch", null, null, true, false);
        User patched = new User(1L, "testPatch@example.com", "Test", "User", "passwordPatch", null, null, true, false);
        // Action
        mapper.patchUserFromDto(requestUserPatchDtoNullEmail, userToPatch);
        // Assert
        assertEquals(patched, userToPatch);
    }

}
