package com.futurasmus.users_api.domain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Page<User> findAll(RequestUserFilterDto filter, Pageable pageable);
    void deleteById(Long id);
}
