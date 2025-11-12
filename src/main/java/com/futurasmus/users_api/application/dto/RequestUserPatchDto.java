package com.futurasmus.users_api.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RequestUserPatchDto(
    @Email @Size(min = 5, max = 100) String email,
    @Size(min = 2, max = 100) String firstName,
    @Size(min = 2, max = 100) String lastName,
    @Size(min = 8, max = 100) String password
) {
    public RequestUserPatchDto withEmail(String email){
        return new RequestUserPatchDto(email, this.firstName, this.lastName, this.password);
    }
    public RequestUserPatchDto withPassword(String password){
        return new RequestUserPatchDto(this.email, this.firstName, this.lastName, password);
    }
    public RequestUserPatchDto withEmailAndPassword(String email, String password){
        return new RequestUserPatchDto(email, this.firstName, this.lastName, password);
    }
}
