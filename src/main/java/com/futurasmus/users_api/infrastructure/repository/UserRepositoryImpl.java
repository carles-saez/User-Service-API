package com.futurasmus.users_api.infrastructure.repository;

import com.futurasmus.users_api.application.dto.RequestUserFilterDto;
import com.futurasmus.users_api.common.mapper.UserMapper;
import com.futurasmus.users_api.domain.model.User;
import com.futurasmus.users_api.domain.repository.UserRepository;
import com.futurasmus.users_api.infrastructure.entity.UserEntity;
import com.futurasmus.users_api.infrastructure.repository.spec.UserSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    @Transactional
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<User> findAll(RequestUserFilterDto filter, Pageable pageable) {
        var spec = UserSpecification.withFilters(filter);
        return jpaRepository.findAll(spec, pageable).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
