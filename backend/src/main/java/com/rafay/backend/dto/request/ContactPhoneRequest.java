package com.rafay.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactPhoneRequest {

    @NotBlank(message = "Phone number is required")
    @Size(max = 255, message = "Phone number must not exceed 255 characters")
    private String phoneNumber;

    @NotBlank(message = "Phone label is required")
    @Size(max = 255, message = "Label must not exceed 255 characters")
    private String label;
}