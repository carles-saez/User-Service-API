package com.futurasmus.users_api.application.dto;

import java.time.LocalDateTime;

public record ResponseUserDto(
    Long id,
    String email,
    String firstName,
    String lastName,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Boolean active,
    Boolean verified
) {
    
}
