package com.futurasmus.users_api.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.common.mapper.UserMapper;
import com.futurasmus.users_api.domain.model.User;
import com.futurasmus.users_api.domain.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    // CREATE
    public ResponseUserDto createUser(RequestUserDto userDto) {
        userRepository.findByEmail(userDto.email().toLowerCase())
            .ifPresent(u -> { throw new IllegalArgumentException("Email " + u.getEmail() + " already in use"); });

        User user = mapper.toDomain(userDto);
        user.setEmail(user.getEmail().toLowerCase());
        User saved = userRepository.save(user);
        return mapper.toResponse(saved);
    }

    // READ
    public Page<ResponseUserDto> getAllUsers(RequestUserFilterDto filter, Pageable pageable) {
        return userRepository.findAll(filter, pageable).map(mapper::toResponse);
    }

    public ResponseUserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return mapper.toResponse(user);
    }

    // UPDATE
    public ResponseUserDto updateUser(Long userId, RequestUserDto userDto) {
        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        existingUser.setEmail(userDto.email().toLowerCase());
        existingUser.setFirstName(userDto.firstName());
        existingUser.setLastName(userDto.lastName());
        existingUser.setPassword(userDto.password());

        User saved = userRepository.save(existingUser);

        return mapper.toResponse(saved);
    }

    // DELETE
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.deleteById(userId);
    }
}
