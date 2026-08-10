package com.rafay.backend.service;

import com.rafay.backend.dto.request.ChangePasswordRequest;
import com.rafay.backend.dto.request.LoginRequest;
import com.rafay.backend.dto.response.ApiResponse;
import com.rafay.backend.dto.response.LoginResponse;
import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rafay.backend.dto.request.RegisterRequest;
import com.rafay.backend.dto.response.RegisterResponse;
import com.rafay.backend.entity.User;
import com.rafay.backend.repository.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository)
    {
        this.userRepository=userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public RegisterResponse registerUser(RegisterRequest request)
    {
        User user = new User();
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        User savedUser = userRepository.save(user);

        RegisterResponse response = new RegisterResponse();
        response.setId(savedUser.getId());
        response.setFirstName(savedUser.getFirstName());
        response.setLastName(savedUser.getLastName());
        response.setEmail(savedUser.getEmail());
        response.setPhoneNumber(savedUser.getPhoneNumber());
        response.setMessage("User registered successfully");

        return response;
    }

    public LoginResponse loginUser(@Valid LoginRequest request) {
        User user = userRepository.findAll().stream()
                .filter(existingUser ->
                        (existingUser.getEmail() != null && existingUser.getEmail().equalsIgnoreCase(request.getIdentifier()))
                                || (existingUser.getPhoneNumber() != null && existingUser.getPhoneNumber().equals(request.getIdentifier()))
                )
                .findFirst()
                .orElse(null);

        LoginResponse response = new LoginResponse();

        if (user != null && request.getPassword() != null && passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            response.setMessage("Login successful");
        } else {
            response.setMessage("Invalid credentials");
        }

        return response;
    }

    public ApiResponse changePassword(@Valid ChangePasswordRequest request) {
        ApiResponse response = new ApiResponse();
        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            response.setMessage("New password cannot be the same as the current password");
            return response;
        }

        User user = userRepository.findAll().stream()
                .filter(existingUser -> existingUser.getPassword() != null
                        && passwordEncoder.matches(request.getCurrentPassword(), existingUser.getPassword()))
                .findFirst()
                .orElse(null);

        if (user != null) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            response.setMessage("Password changed successfully");
            response.setSuccess(true);
        } else {
            response.setMessage("Current password is incorrect");
        }
        return response;
    }
}
