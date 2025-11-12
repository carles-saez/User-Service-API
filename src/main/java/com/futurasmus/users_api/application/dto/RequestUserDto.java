package com.futurasmus.users_api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RequestUserDto(
    @Email @NotBlank @Size(min = 5, max = 100) String email,
    @Size(min = 3, max = 100) String firstName,
    @Size(min = 3, max = 100) String lastName,
    @NotBlank @Size(min = 8, max = 100) String password
) {
    public RequestUserDto withEmail(String email){
        return new RequestUserDto(email, this.firstName, this.lastName, this.password);
    }
    public RequestUserDto withPassword(String password){
        return new RequestUserDto(this.email, this.firstName, this.lastName, password);
    }
    public RequestUserDto withEmailAndPassword(String email, String password){
        return new RequestUserDto(email, this.firstName, this.lastName, password);
    }
}
