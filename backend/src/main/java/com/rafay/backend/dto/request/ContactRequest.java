package com.rafay.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class ContactRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 255, message = "First name must not exceed 255 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 255, message = "Last name must not exceed 255 characters")
    private String lastName;

    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @Valid
    private List<ContactEmailRequest> emails;

    @Valid
    private List<ContactPhoneRequest> phoneNumbers;
}