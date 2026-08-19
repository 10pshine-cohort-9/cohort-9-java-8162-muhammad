package com.rafay.backend.repository;

import com.rafay.backend.entity.ContactPhone;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactPhoneRepository extends JpaRepository<ContactPhone, Long> {
    List<ContactPhone> findByContactId(Long contactId);
}