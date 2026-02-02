package com.example.demcayniki.model.requests;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank
    @Email
    public String email;
    @NotBlank
    public String password;
}
