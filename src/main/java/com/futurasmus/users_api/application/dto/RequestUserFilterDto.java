package com.futurasmus.users_api.application.dto;

import java.time.LocalDateTime;

public record RequestUserFilterDto(
    String email,
    String firstName,
    String lastName,
    String notEmail,
    String notFirstName,
    String notLastName,
    LocalDateTime createdBefore,
    LocalDateTime createdAfter,
    LocalDateTime updatedBefore,
    LocalDateTime updatedAfter,
    Boolean active,
    Boolean verified) {
}
