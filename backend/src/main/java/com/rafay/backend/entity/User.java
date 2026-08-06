package com.rafay.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String firstName;


    @Column(nullable = false)
    private String lastName;


    @Column(unique = true)
    private String email;


    @Column(unique = true)
    private String phoneNumber;


    @Column(nullable = false)
    private String password;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    //runs when creating entity
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    //runs when updating entity
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}