package com.rafay.backend.repository;

import com.rafay.backend.entity.ContactEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactEmailRepository extends JpaRepository<ContactEmail, Long> {
    List<ContactEmail> findByContactId(Long contactId);
}