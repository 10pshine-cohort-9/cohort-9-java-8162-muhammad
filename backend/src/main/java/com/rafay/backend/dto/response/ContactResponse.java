package com.rafay.backend.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContactResponse {

    private Long id;

    private String firstName;

    private String lastName;

    private String title;

    private List<ContactEmailResponse> emails;

    private List<ContactPhoneResponse> phoneNumbers;
}