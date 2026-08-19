package com.rafay.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactEmailResponse {

    private Long id;

    private String email;

    private String label;
}