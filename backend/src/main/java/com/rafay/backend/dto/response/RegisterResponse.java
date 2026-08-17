package com.rafay.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String message;

    // Getters and Setters
}