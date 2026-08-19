package com.rafay.backend.service;

import com.rafay.backend.dto.request.ContactRequest;
import com.rafay.backend.dto.response.ContactResponse;
import com.rafay.backend.entity.Contact;
import com.rafay.backend.entity.ContactEmail;
import com.rafay.backend.entity.ContactPhone;
import com.rafay.backend.entity.User;
import com.rafay.backend.repository.ContactEmailRepository;
import com.rafay.backend.repository.ContactPhoneRepository;
import com.rafay.backend.repository.ContactRepository;
import com.rafay.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class ContactService {

    private final ContactRepository contactRepository;
    private final ContactEmailRepository contactEmailRepository;
    private final ContactPhoneRepository contactPhoneRepository;
    private final UserRepository userRepository;

    public ContactService(
            ContactRepository contactRepository,
            ContactEmailRepository contactEmailRepository,
            ContactPhoneRepository contactPhoneRepository,
            UserRepository userRepository) {

        this.contactRepository = contactRepository;
        this.contactEmailRepository = contactEmailRepository;
        this.contactPhoneRepository = contactPhoneRepository;
        this.userRepository = userRepository;
    }

    public ContactResponse createContact(
            ContactRequest request,
            String userEmail) {

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Contact contact = new Contact();

        contact.setFirstName(request.getFirstName());
        contact.setLastName(request.getLastName());
        contact.setTitle(request.getTitle());
        contact.setUser(user);

        Contact savedContact = contactRepository.save(contact);

        // Add emails
        if (request.getEmails() != null) {

            request.getEmails().forEach(emailRequest -> {

                ContactEmail email = new ContactEmail();

                email.setEmail(emailRequest.getEmail());
                email.setLabel(emailRequest.getLabel());
                email.setContact(savedContact);

                contactEmailRepository.save(email);
            });
        }

        // Add phone numbers
        if (request.getPhoneNumbers() != null) {

            request.getPhoneNumbers().forEach(phoneRequest -> {

                ContactPhone phone = new ContactPhone();

                phone.setPhoneNumber(phoneRequest.getPhoneNumber());
                phone.setLabel(phoneRequest.getLabel());
                phone.setContact(savedContact);

                contactPhoneRepository.save(phone);
            });
        }

        return mapToResponse(savedContact);
    }

    private ContactResponse mapToResponse(Contact contact) {

        ContactResponse response = new ContactResponse();

        response.setId(contact.getId());
        response.setFirstName(contact.getFirstName());
        response.setLastName(contact.getLastName());
        response.setTitle(contact.getTitle());

        return response;
    }
}