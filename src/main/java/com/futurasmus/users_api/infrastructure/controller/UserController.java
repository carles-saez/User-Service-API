package com.futurasmus.users_api.infrastructure.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.PageResponse;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.application.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // CREATE
    @PostMapping
    public ResponseUserDto createUser(@Valid @RequestBody RequestUserDto user) {
        return userService.createUser(user);
    }

    // READ
    @GetMapping
    public PageResponse<ResponseUserDto> getAllUsers(@PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ResponseUserDto> page = userService.getAllUsers(pageable);
        return new PageResponse<>(page);
    }
    
    @GetMapping("/{userId}")
    public ResponseUserDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }
    
    // UPDATE
    @PutMapping("/{userId}")
    public ResponseUserDto updateUser(@PathVariable Long userId, @Valid @RequestBody RequestUserDto userDto) {
        return userService.updateUser(userId, userDto);
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
    
}
