package com.futurasmus.users_api.common.mapper;

import com.futurasmus.users_api.application.dto.RequestUserDto;
import com.futurasmus.users_api.application.dto.ResponseUserDto;
import com.futurasmus.users_api.domain.model.User;
import com.futurasmus.users_api.infrastructure.entity.UserEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toDomain(UserEntity entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "validated", ignore = true)
    User toDomain(RequestUserDto entity);

    UserEntity toEntity(User domain);
    ResponseUserDto toResponse(User domain);
}