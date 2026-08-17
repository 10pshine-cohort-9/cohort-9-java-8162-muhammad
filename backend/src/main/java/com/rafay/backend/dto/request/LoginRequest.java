package com.rafay.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

    @NotBlank
    private String identifier; // email or phone number

    @NotBlank
    private String password;

    // Getters and Setters
}