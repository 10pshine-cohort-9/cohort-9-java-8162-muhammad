package com.rafay.backend.service;

import com.rafay.backend.dto.request.ContactRequest;
import com.rafay.backend.dto.response.ContactEmailResponse;
import com.rafay.backend.dto.response.ContactPhoneResponse;
import com.rafay.backend.dto.response.ContactResponse;
import com.rafay.backend.entity.Contact;
import com.rafay.backend.entity.ContactEmail;
import com.rafay.backend.entity.ContactPhone;
import com.rafay.backend.entity.User;
import com.rafay.backend.exception.ResourceNotFoundException;
import com.rafay.backend.repository.ContactEmailRepository;
import com.rafay.backend.repository.ContactPhoneRepository;
import com.rafay.backend.repository.ContactRepository;
import com.rafay.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private static final Logger logger =
            LoggerFactory.getLogger(ContactService.class);

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

    @Transactional
    public ContactResponse createContact(
            ContactRequest request,
            String userEmail) {

        logger.info(
                "Creating contact for user: {}",
                userEmail
        );

        User user =
                userRepository
                        .findByEmail(userEmail)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"
                                )
                        );

        Contact contact =
                new Contact();

        contact.setFirstName(
                request.getFirstName()
        );

        contact.setLastName(
                request.getLastName()
        );

        contact.setTitle(
                request.getTitle()
        );

        contact.setUser(user);

        Contact savedContact =
                contactRepository.save(contact);

        if (request.getEmails() != null) {

            request.getEmails()
                    .forEach(emailRequest -> {

                        ContactEmail email =
                                new ContactEmail();

                        email.setEmail(
                                emailRequest.getEmail()
                        );

                        email.setLabel(
                                emailRequest.getLabel()
                        );

                        email.setContact(
                                savedContact
                        );

                        contactEmailRepository.save(email);
                    });
        }

        if (request.getPhoneNumbers() != null) {

            request.getPhoneNumbers()
                    .forEach(phoneRequest -> {

                        ContactPhone phone =
                                new ContactPhone();

                        phone.setPhoneNumber(
                                phoneRequest.getPhoneNumber()
                        );

                        phone.setLabel(
                                phoneRequest.getLabel()
                        );

                        phone.setContact(
                                savedContact
                        );

                        contactPhoneRepository.save(phone);
                    });
        }

        logger.info(
                "Contact created successfully. Contact ID: {}, User: {}",
                savedContact.getId(),
                userEmail
        );

        return mapToResponse(savedContact);
    }

    public ContactResponse getContact(
            Long contactId,
            String userEmail) {

        logger.info(
                "Fetching contact ID: {} for user: {}",
                contactId,
                userEmail
        );

        Contact contact =
                contactRepository
                        .findByIdAndUserEmail(
                                contactId,
                                userEmail
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contact not found"
                                )
                        );

        logger.info(
                "Contact ID: {} retrieved successfully for user: {}",
                contactId,
                userEmail
        );

        return mapToResponse(contact);
    }

    public Page<ContactResponse> getContacts(
            String userEmail,
            Pageable pageable) {

        logger.info(
                "Fetching contacts for user: {}, page: {}, size: {}",
                userEmail,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Contact> contacts =
                contactRepository.findByUserEmail(
                        userEmail,
                        pageable
                );

        return contacts.map(this::mapToResponse);
    }

    public Page<ContactResponse> searchContacts(
            String userEmail,
            String search,
            Pageable pageable) {

        logger.info(
                "Searching contacts for user: {}, search: '{}', page: {}, size: {}",
                userEmail,
                search,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<Contact> contacts =
                contactRepository
                        .findByUserEmailAndFirstNameContainingIgnoreCaseOrUserEmailAndLastNameContainingIgnoreCase(
                                userEmail,
                                search,
                                userEmail,
                                search,
                                pageable
                        );

        return contacts.map(this::mapToResponse);
    }

    private ContactResponse mapToResponse(
            Contact contact) {

        ContactResponse response =
                new ContactResponse();

        response.setId(
                contact.getId()
        );

        response.setFirstName(
                contact.getFirstName()
        );

        response.setLastName(
                contact.getLastName()
        );

        response.setTitle(
                contact.getTitle()
        );

        List<ContactEmailResponse> emails =
                contactEmailRepository
                        .findByContactId(
                                contact.getId()
                        )
                        .stream()
                        .map(email -> {

                            ContactEmailResponse emailResponse =
                                    new ContactEmailResponse();

                            emailResponse.setId(
                                    email.getId()
                            );

                            emailResponse.setEmail(
                                    email.getEmail()
                            );

                            emailResponse.setLabel(
                                    email.getLabel()
                            );

                            return emailResponse;
                        })
                        .toList();

        response.setEmails(emails);

        List<ContactPhoneResponse> phoneNumbers =
                contactPhoneRepository
                        .findByContactId(
                                contact.getId()
                        )
                        .stream()
                        .map(phone -> {

                            ContactPhoneResponse phoneResponse =
                                    new ContactPhoneResponse();

                            phoneResponse.setId(
                                    phone.getId()
                            );

                            phoneResponse.setPhoneNumber(
                                    phone.getPhoneNumber()
                            );

                            phoneResponse.setLabel(
                                    phone.getLabel()
                            );

                            return phoneResponse;
                        })
                        .toList();

        response.setPhoneNumbers(
                phoneNumbers
        );

        return response;
    }

    @Transactional
    public ContactResponse updateContact(
            Long contactId,
            ContactRequest request,
            String userEmail) {

        logger.info(
                "Updating contact ID: {} for user: {}",
                contactId,
                userEmail
        );

        Contact contact =
                contactRepository
                        .findByIdAndUserEmail(
                                contactId,
                                userEmail
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contact not found"
                                )
                        );

        contact.setFirstName(
                request.getFirstName()
        );

        contact.setLastName(
                request.getLastName()
        );

        contact.setTitle(
                request.getTitle()
        );

        Contact updatedContact =
                contactRepository.save(contact);

        List<ContactEmail> existingEmails =
                contactEmailRepository
                        .findByContactId(contactId);

        contactEmailRepository.deleteAll(
                existingEmails
        );

        if (request.getEmails() != null) {

            request.getEmails()
                    .forEach(emailRequest -> {

                        ContactEmail email =
                                new ContactEmail();

                        email.setEmail(
                                emailRequest.getEmail()
                        );

                        email.setLabel(
                                emailRequest.getLabel()
                        );

                        email.setContact(
                                updatedContact
                        );

                        contactEmailRepository.save(email);
                    });
        }

        List<ContactPhone> existingPhones =
                contactPhoneRepository
                        .findByContactId(contactId);

        contactPhoneRepository.deleteAll(
                existingPhones
        );

        if (request.getPhoneNumbers() != null) {

            request.getPhoneNumbers()
                    .forEach(phoneRequest -> {

                        ContactPhone phone =
                                new ContactPhone();

                        phone.setPhoneNumber(
                                phoneRequest.getPhoneNumber()
                        );

                        phone.setLabel(
                                phoneRequest.getLabel()
                        );

                        phone.setContact(
                                updatedContact
                        );

                        contactPhoneRepository.save(phone);
                    });
        }

        logger.info(
                "Contact ID: {} updated successfully for user: {}",
                contactId,
                userEmail
        );

        return mapToResponse(updatedContact);
    }

    @Transactional
    public void deleteContact(
            Long contactId,
            String userEmail) {

        logger.info(
                "Deleting contact ID: {} for user: {}",
                contactId,
                userEmail
        );

        Contact contact =
                contactRepository
                        .findByIdAndUserEmail(
                                contactId,
                                userEmail
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Contact not found"
                                )
                        );

        contactEmailRepository.deleteAll(
                contactEmailRepository
                        .findByContactId(contactId)
        );

        contactPhoneRepository.deleteAll(
                contactPhoneRepository
                        .findByContactId(contactId)
        );

        contactRepository.delete(contact);

        logger.info(
                "Contact ID: {} deleted successfully for user: {}",
                contactId,
                userEmail
        );
    }
}