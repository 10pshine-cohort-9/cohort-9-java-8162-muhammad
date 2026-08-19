package com.rafay.backend.controller;

import com.rafay.backend.dto.request.ContactRequest;
import com.rafay.backend.dto.response.ContactResponse;
import com.rafay.backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public ResponseEntity<ContactResponse> createContact(
            @Valid @RequestBody ContactRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        ContactResponse response =
                contactService.createContact(request, userEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping
    public ResponseEntity<Page<ContactResponse>> getContacts(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = authentication.getName();

        Page<ContactResponse> response =
                contactService.getContacts(
                        userEmail,
                        pageable
                );

        return ResponseEntity.ok(response);
    }
}