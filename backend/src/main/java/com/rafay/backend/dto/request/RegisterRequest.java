package com.rafay.backend.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    @NotBlank
    @JsonProperty("firstName")
    @JsonAlias({"firstname"})
    private String firstname;

    @NotBlank
    @JsonProperty("lastName")
    @JsonAlias({"lastname"})
    private String lastname;

    @NotBlank
    @Email
    @JsonProperty("email")
    private String email;

    @JsonProperty("phoneNumber")
    private String phoneNumber;

    @NotBlank
    @JsonProperty("password")
    private String password;
}
