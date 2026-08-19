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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
@RestController
@RequestMapping("/api/v1/contacts")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }
    @PutMapping("/{id}")
    public ResponseEntity<ContactResponse> updateContact(
            @PathVariable Long id,
            @RequestBody @Valid ContactRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        ContactResponse response =
                contactService.updateContact(
                        id,
                        request,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        contactService.deleteContact(
                id,
                userEmail
        );

        return ResponseEntity.noContent().build();
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
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        String userEmail = authentication.getName();

        Page<ContactResponse> response;

        if (search == null || search.trim().isEmpty()) {

            response = contactService.getContacts(
                    userEmail,
                    pageable
            );

        } else {

            response = contactService.searchContacts(
                    userEmail,
                    search.trim(),
                    pageable
            );
        }

        return ResponseEntity.ok(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContact(
            @PathVariable Long id,
            Authentication authentication) {

        String userEmail = authentication.getName();

        ContactResponse response =
                contactService.getContact(
                        id,
                        userEmail
                );

        return ResponseEntity.ok(response);
    }
}