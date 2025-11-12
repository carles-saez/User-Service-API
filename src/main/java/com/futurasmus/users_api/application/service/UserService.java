package com.futurasmus.users_api.application.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.application.dto.RequestUserPatchDto;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.common.exception.EmailAlreadyExistsException;
import com.futurasmus.users_api.common.exception.UserNotFoundException;
import com.futurasmus.users_api.common.mapper.UserMapper;
import com.futurasmus.users_api.domain.model.User;
import com.futurasmus.users_api.domain.repository.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // CREATE
    @Transactional
    public ResponseUserDto createUser(RequestUserDto userDto) {
        // RequestUserDto normUserDto = userDto.withEmail(userDto.email().toLowerCase());
        RequestUserDto normUserDto = userDto.withEmailAndPassword(userDto.email().toLowerCase(), passwordEncoder.encode(userDto.password()));
        userRepository.findByEmail(normUserDto.email())
            .ifPresent(u -> { throw new EmailAlreadyExistsException(u.getEmail()); });
        User user = mapper.toDomain(normUserDto);
        User saved = userRepository.save(user);
        return mapper.toResponse(saved);
    }

    // READ
    @Transactional(readOnly = true)
    public Page<ResponseUserDto> getAllUsers(RequestUserFilterDto filter, Pageable pageable) {
        return userRepository.findAll(filter, pageable).map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public ResponseUserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        return mapper.toResponse(user);
    }

    // UPDATE
    @Transactional
    public ResponseUserDto updateUser(Long userId, RequestUserDto userDto) {
        // RequestUserDto normUserDto = userDto.withEmail(userDto.email().toLowerCase());
        RequestUserDto normUserDto = userDto.withEmailAndPassword(userDto.email().toLowerCase(), passwordEncoder.encode(userDto.password()));

        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
                
        userRepository.findByEmail(normUserDto.email())
            .ifPresent(u -> { throw new EmailAlreadyExistsException(u.getEmail()); });

        mapper.updateUserFromDto(normUserDto, existingUser);
        User saved = userRepository.save(existingUser);
        return mapper.toResponse(saved);
    }

    @Transactional
    public ResponseUserDto updateUserPartial(Long userId, RequestUserPatchDto userDto) {

        RequestUserPatchDto normUserDto = userDto;
        if (userDto.email() != null) {
            normUserDto = userDto.withEmail(userDto.email().toLowerCase());
        }
        if (userDto.password() != null) {
            normUserDto = userDto.withPassword(passwordEncoder.encode(userDto.password()));
        }

        User existingUser = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
            
        if (normUserDto.email() != null) {
            userRepository.findByEmail(normUserDto.email())
                .ifPresent(u -> { throw new EmailAlreadyExistsException(u.getEmail()); });
        }
        mapper.patchUserFromDto(normUserDto, existingUser);
        User saved = userRepository.save(existingUser);
        return mapper.toResponse(saved);
    }

    // DELETE
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException(userId));
        userRepository.deleteById(userId);
    }

}
