package com.rafay.backend.repository;

import com.rafay.backend.entity.Contact;
import com.rafay.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Optional<Contact> findByIdAndUser(Long id, User user);
}