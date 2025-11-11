package com.futurasmus.users_api.infrastructure.controller;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.application.dto.ResponsePage;
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
    public ResponseEntity<ResponseUserDto> createUser(@Valid @RequestBody RequestUserDto user) {
        ResponseUserDto saved = userService.createUser(user);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
            .body(saved);
    }

    // READ
    @GetMapping
    public ResponseEntity<ResponsePage<ResponseUserDto>> getAllUsers(@ModelAttribute RequestUserFilterDto filter,
                                                     @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        Page<ResponseUserDto> page = userService.getAllUsers(filter, pageable);
        return ResponseEntity.ok(new ResponsePage<>(page));
    }
    
    @GetMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    
    // UPDATE
    @PutMapping("/{userId}")
    public ResponseEntity<ResponseUserDto> updateUser(@PathVariable Long userId, @Valid @RequestBody RequestUserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(userId, userDto));
    }

    // DELETE
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    
}
