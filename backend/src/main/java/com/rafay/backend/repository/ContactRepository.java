package com.rafay.backend.repository;

import com.rafay.backend.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Page<Contact> findByUserEmail(
            String email,
            Pageable pageable
    );
}