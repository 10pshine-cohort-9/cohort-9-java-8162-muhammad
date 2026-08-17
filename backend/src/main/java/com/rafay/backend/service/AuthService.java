package com.rafay.backend.service;

import com.rafay.backend.dto.request.ChangePasswordRequest;
import com.rafay.backend.dto.request.LoginRequest;
import com.rafay.backend.dto.response.ApiResponse;
import com.rafay.backend.dto.response.LoginResponse;
import com.rafay.backend.exception.ConflictException;
import com.rafay.backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rafay.backend.dto.request.RegisterRequest;
import com.rafay.backend.dto.response.RegisterResponse;
import com.rafay.backend.entity.User;
import com.rafay.backend.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    public AuthService(UserRepository userRepository, JwtService jwtService)
    {
        this.userRepository=userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }
    public RegisterResponse registerUser(RegisterRequest request) {
        User user = new User();
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        RegisterResponse response;
        try {
            User savedUser = userRepository.save(user);
            response = new RegisterResponse();
            response.setId(savedUser.getId());
            response.setFirstName(savedUser.getFirstName());
            response.setLastName(savedUser.getLastName());
            response.setEmail(savedUser.getEmail());
            response.setPhoneNumber(savedUser.getPhoneNumber());
            response.setMessage("User registered successfully");
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("Email or phone number already exists");
        }
        return response;
    }

    public LoginResponse loginUser(@Valid LoginRequest request) {
        User user = userRepository.findAll().stream()
                //find a user by email or phone number
                .filter(existingUser ->
                        (existingUser.getEmail() != null && existingUser.getEmail().equalsIgnoreCase(request.getIdentifier()))
                                || (existingUser.getPhoneNumber() != null && existingUser.getPhoneNumber().equals(request.getIdentifier()))
                )
                .findFirst()
                .orElse(null);

        LoginResponse response = new LoginResponse();

        if (user != null
                && request.getPassword() != null
                && passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            String token = jwtService.generateToken(
                    user.getEmail()
            );

            response.setMessage("Login successful");
            response.setToken(token);

        } else {

            response.setMessage("Invalid credentials");
        }

        return response;
    }

    public ApiResponse changePassword(@Valid ChangePasswordRequest request) {

        ApiResponse response = new ApiResponse();

        if (request.getCurrentPassword().equals(request.getNewPassword())) {
            response.setMessage(
                    "New password cannot be the same as the current password"
            );
            return response;
        }

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated()) {

            response.setMessage("Unauthorized");
            return response;
        }

        String email = authentication.getName();

        User user = userRepository
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            response.setMessage("User not found");
            return response;
        }

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            response.setMessage("Current password is incorrect");
            return response;
        }

        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );

        userRepository.save(user);

        response.setMessage("Password changed successfully");
        response.setSuccess(true);

        return response;
    }
}
