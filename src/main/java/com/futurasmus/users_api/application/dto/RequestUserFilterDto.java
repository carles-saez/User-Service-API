package com.futurasmus.users_api.application.dto;

public record RequestUserFilterDto(
    String email,
    String firstName,
    String lastName,
    Boolean active,
    Boolean verified) {
}
