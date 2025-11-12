package com.futurasmus.users_api.application.dto;

public record RequestUserFilterDto(
    String email,
    String firstName,
    String lastName,
    String notEmail,
    String notFirstName,
    String notLastName,
    Boolean active,
    Boolean verified) {
}
