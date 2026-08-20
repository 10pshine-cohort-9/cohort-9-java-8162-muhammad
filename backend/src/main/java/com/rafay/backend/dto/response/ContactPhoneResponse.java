package com.rafay.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactPhoneResponse {

    private Long id;

    private String phoneNumber;

    private String label;
}