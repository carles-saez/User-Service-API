package com.futurasmus.users_api.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.application.dto.RequestUserPatchDto;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.common.exception.EmailAlreadyExistsException;
import com.futurasmus.users_api.common.exception.UserNotFoundException;
import com.futurasmus.users_api.common.mapper.UserMapper;
import com.futurasmus.users_api.domain.model.User;
import com.futurasmus.users_api.domain.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper mapper;
    

    // --- CREATE ---
    @Test
    void createUser_withCorrectUserDto_shouldReturn_ResponseUserDto() {
        // Arrange
        RequestUserDto userDto = new RequestUserDto("test@example.com", "Test", "User", "password");
        User user = new User(1L, "test@example.com", "Test", "User", "password", null, null, true, false);
        ResponseUserDto responseUserDto = new ResponseUserDto(1L, "test@example.com", "Test", "User", null, null, true, false);
        
        when(userRepository.findByEmail(userDto.email().toLowerCase())).thenReturn(Optional.empty());
        when(mapper.toDomain(userDto)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(mapper.toResponse(user)).thenReturn(responseUserDto);
        
        // Action
        ResponseUserDto result = userService.createUser(userDto);
        
        // Assert
        assertEquals(responseUserDto, result);
        
        verify(userRepository).findByEmail(userDto.email().toLowerCase());
        verify(mapper).toDomain(userDto);
        verify(userRepository).save(any(User.class));
        verify(mapper).toResponse(user);
    }
    
    // --- CREATE: email conflict ---
    @Test
    void createUser_withExistingEmail_shouldThrow_EmailAlreadyExistsException() {
        // Arrange
        RequestUserDto userDto = new RequestUserDto("Test", "User", "test@example.com", "password");
        User user = new User(1L, "test@example.com", "Test", "User", "password", null, null, true, false);
        
        when(userRepository.findByEmail(userDto.email().toLowerCase())).thenReturn(Optional.of(user));

        // Action & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.createUser(userDto));

        verify(userRepository).findByEmail(userDto.email().toLowerCase());
        verify(userRepository, times(0)).save(any(User.class));
    }
    
    // --- GET ALL ---
    @Test
    void getAllUsers_shouldReturn_PageOfResponseUserDto() {
        // Arrange
        RequestUserFilterDto filter = new RequestUserFilterDto("test", null, null, null, null);
        Pageable pageable = PageRequest.of(0, 2);

        User user1 = new User(1L, "test1@example.com", "Test1", "User1", null, null, null, true, false);
        User user2 = new User(2L, "test2@example.com", "Test2", "User2", null, null, null, true, false);

        List<User> users = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(users, pageable, 2);

        ResponseUserDto dto1 = new ResponseUserDto(1L, "test1@example.com", "Test1", "User1", null, null, true, false);
        ResponseUserDto dto2 = new ResponseUserDto(2L, "test2@example.com", "Test2", "User2", null, null, true, false);

        when(userRepository.findAll(filter, pageable)).thenReturn(userPage);
        when(mapper.toResponse(user1)).thenReturn(dto1);
        when(mapper.toResponse(user2)).thenReturn(dto2);

        // Action
        Page<ResponseUserDto> result = userService.getAllUsers(filter, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
        assertEquals("test1@example.com", result.getContent().get(0).email());
        assertEquals("test2@example.com", result.getContent().get(1).email());

        verify(userRepository).findAll(filter, pageable);
        verify(mapper, times(2)).toResponse(any(User.class));
    }

    // --- GET BY ID ---
    @Test
    void getUserById_withValidId_shouldReturn_ResponseUserDto() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "test@example.com", "Test", "User", "password", null, null, true, false);
        ResponseUserDto responseUserDto = new ResponseUserDto(userId, "test@example.com", "Test", "User", null, null, true, false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(mapper.toResponse(user)).thenReturn(responseUserDto);
        
        // Action
        ResponseUserDto result = userService.getUserById(userId);
        
        // Assert
        assertEquals(responseUserDto, result);
        
        verify(userRepository).findById(userId);
        verify(mapper).toResponse(user);
    }
    
    // --- GET BY ID: not found ---
    @Test
    void getUserById_withInvalidId_shouldThrow_UserNotFoundException() {
        // Arrange
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Action & Assert
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        
        verify(userRepository).findById(userId);
        verify(mapper, times(0)).toResponse(any(User.class));
    }

    // --- UPDATE ---
    @Test
    void updateUser_withValidIdAndDto_shouldReturn_ResponseUserDto() {
        // Arrange
        Long userId = 1L;
        RequestUserDto userDto = new RequestUserDto("updated@example.com", "Updated", "User", "newpassword");
        User existingUser = new User(userId, "old@example.com", "Old", "User", "oldpassword", null, null, true, false);
        ResponseUserDto responseUserDto = new ResponseUserDto(userId, "updated@example.com", "Updated", "User", null, null, true, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDto.email().toLowerCase())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            RequestUserDto argDto = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setEmail(argDto.email().toLowerCase());
            target.setFirstName(argDto.firstName());
            target.setLastName(argDto.lastName());
            target.setPassword(argDto.password());
            return null;
        }).when(mapper).updateUserFromDto(eq(userDto), any(User.class));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(User.class))).thenReturn(responseUserDto);

        // Action
        ResponseUserDto result = userService.updateUser(userId, userDto);

        // Assert
        assertEquals(responseUserDto, result);
        assertEquals("updated@example.com", existingUser.getEmail());

        verify(userRepository).findById(userId);
        verify(mapper).updateUserFromDto(userDto, existingUser);
        verify(userRepository).save(existingUser);
        verify(mapper).toResponse(existingUser);
    }
    
    // --- UPDATE: not found ---
    @Test
    void updateUser_withInvalidId_shouldThrow_UserNotFoundException() {
        // Arrange
        Long userId = 999L;
        RequestUserDto userDto = new RequestUserDto("Updated", "User", "updated@example.com", "newpassword");
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Action & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, userDto));
        
        verify(userRepository).findById(userId);
        verify(userRepository, times(0)).findByEmail(any(String.class));
        verify(mapper, times(0)).updateUserFromDto(any(RequestUserDto.class), any(User.class));
    }
    
    // --- UPDATE: email conflict ---
    @Test
    void updateUser_withExistingEmail_shouldThrow_EmailAlreadyExistsException() {
        // Arrange
        Long userId = 1L;
        RequestUserDto userDto = new RequestUserDto("Updated", "User", "existing@example.com", "newpassword");
        User existingUser = new User(userId, "old@example.com", "Old", "User", "oldpassword", null, null, true, false);
        User anotherUser = new User(2L, "existing@example.com", "Another", "User", "password", null, null, true, false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDto.email().toLowerCase())).thenReturn(Optional.of(anotherUser));
        
        // Action & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUser(userId, userDto));
        
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(userDto.email().toLowerCase());
        verify(mapper, times(0)).updateUserFromDto(any(RequestUserDto.class), any(User.class));
    }

    

    // --- PARTIAL UPDATE ---
    @Test
    void updateUserPartial_withValidIdAndDto_shouldReturn_ResponseUserDto() {
        // Arrange
        Long userId = 1L;
        RequestUserPatchDto userDto = new RequestUserPatchDto("updated@example.com", "Updated", "User", "newpassword");
        User existingUser = new User(userId, "old@example.com", "Old", "User", "oldpassword", null, null, true, false);
        ResponseUserDto responseUserDto = new ResponseUserDto(userId, "updated@example.com", "Updated", "User", null, null, true, false);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDto.email().toLowerCase())).thenReturn(Optional.empty());
        doAnswer(invocation -> {
            RequestUserPatchDto argDto = invocation.getArgument(0);
            User target = invocation.getArgument(1);
            target.setEmail(argDto.email().toLowerCase());
            target.setFirstName(argDto.firstName());
            target.setLastName(argDto.lastName());
            target.setPassword(argDto.password());
            return null;
        }).when(mapper).patchUserFromDto(eq(userDto), any(User.class));

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toResponse(any(User.class))).thenReturn(responseUserDto);

        // Action
        ResponseUserDto result = userService.updateUserPartial(userId, userDto);

        // Assert
        assertEquals(responseUserDto, result);
        assertEquals("updated@example.com", existingUser.getEmail());

        verify(userRepository).findById(userId);
        verify(mapper).patchUserFromDto(userDto, existingUser);
        verify(userRepository).save(existingUser);
        verify(mapper).toResponse(existingUser);
    }
    
    // --- PARTIAL UPDATE: not found ---
    @Test
    void updateUserPartial_withInvalidId_shouldThrow_UserNotFoundException() {
        // Arrange
        Long userId = 999L;
        RequestUserPatchDto userDto = new RequestUserPatchDto("Updated", "User", "updated@example.com", "newpassword");
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Action & Assert
        assertThrows(UserNotFoundException.class, () -> userService.updateUserPartial(userId, userDto));
        
        verify(userRepository).findById(userId);
        verify(userRepository, times(0)).findByEmail(any(String.class));
        verify(mapper, times(0)).patchUserFromDto(any(RequestUserPatchDto.class), any(User.class));
    }
    
    // --- PARTIAL UPDATE: email conflict ---
    @Test
    void updateUserPartial_withExistingEmail_shouldThrow_EmailAlreadyExistsException() {
        // Arrange
        Long userId = 1L;
        RequestUserPatchDto userDto = new RequestUserPatchDto("Updated", "User", "existing@example.com", "newpassword");
        User existingUser = new User(userId, "old@example.com", "Old", "User", "oldpassword", null, null, true, false);
        User anotherUser = new User(2L, "existing@example.com", "Another", "User", "password", null, null, true, false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userDto.email().toLowerCase())).thenReturn(Optional.of(anotherUser));
        
        // Action & Assert
        assertThrows(EmailAlreadyExistsException.class, () -> userService.updateUserPartial(userId, userDto));
        
        verify(userRepository).findById(userId);
        verify(userRepository).findByEmail(userDto.email().toLowerCase());
        verify(mapper, times(0)).patchUserFromDto(any(RequestUserPatchDto.class), any(User.class));
    }
    
    // --- DELETE ---
    @Test
    void deleteUser_withValidId_shouldDeleteUser() {
        // Arrange
        Long userId = 1L;
        User user = new User(userId, "test@example.com", "Test", "User", "password", null, null, true, false);
        
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        
        // Action
        userService.deleteUser(userId);
        
        // Assert
        verify(userRepository).findById(userId);
        verify(userRepository).deleteById(userId);
    }
    
    // --- DELETE: not found ---
    @Test
    void deleteUser_withInvalidId_shouldThrow_UserNotFoundException() {
        // Arrange
        Long userId = 999L;
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        // Action & Assert
        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        
        verify(userRepository).findById(userId);
        verify(userRepository, times(0)).deleteById(any(Long.class));
    }
}
