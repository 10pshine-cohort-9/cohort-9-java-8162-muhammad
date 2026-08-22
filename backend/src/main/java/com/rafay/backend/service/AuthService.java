package com.rafay.backend.service;

import com.rafay.backend.dto.request.ChangePasswordRequest;
import com.rafay.backend.dto.request.LoginRequest;
import com.rafay.backend.dto.request.RegisterRequest;
import com.rafay.backend.dto.response.ApiResponse;
import com.rafay.backend.dto.response.LoginResponse;
import com.rafay.backend.dto.response.RegisterResponse;
import com.rafay.backend.entity.User;
import com.rafay.backend.exception.ConflictException;
import com.rafay.backend.repository.UserRepository;
import com.rafay.backend.security.JwtService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public RegisterResponse registerUser(
            RegisterRequest request) {

        logger.info(
                "Registration attempt for email: {}",
                request.getEmail()
        );

        User user = new User();

        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        try {

            User savedUser = userRepository.save(user);

            logger.info(
                    "User registered successfully. User ID: {}",
                    savedUser.getId()
            );

            RegisterResponse response =
                    new RegisterResponse();

            response.setId(savedUser.getId());
            response.setFirstName(savedUser.getFirstName());
            response.setLastName(savedUser.getLastName());
            response.setEmail(savedUser.getEmail());
            response.setPhoneNumber(
                    savedUser.getPhoneNumber()
            );
            response.setMessage(
                    "User registered successfully"
            );

            return response;

        } catch (DataIntegrityViolationException ex) {

            logger.warn(
                    "Registration failed because email or phone number already exists: {}",
                    request.getEmail()
            );

            throw new ConflictException(
                    "Email or phone number already exists"
            );
        }
    }

    public LoginResponse loginUser(
            @Valid LoginRequest request) {

        logger.info(
                "Login attempt for identifier: {}",
                request.getIdentifier()
        );

        User user = userRepository.findAll()
                .stream()
                .filter(existingUser ->
                        (existingUser.getEmail() != null
                                && existingUser.getEmail()
                                .equalsIgnoreCase(
                                        request.getIdentifier()
                                ))
                                ||
                                (existingUser.getPhoneNumber() != null
                                        && existingUser
                                        .getPhoneNumber()
                                        .equals(
                                                request.getIdentifier()
                                        ))
                )
                .findFirst()
                .orElse(null);

        LoginResponse response =
                new LoginResponse();

        if (user != null
                && request.getPassword() != null
                && passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            String token =
                    jwtService.generateToken(
                            user.getEmail()
                    );

            logger.info(
                    "Successful login for user: {}",
                    user.getEmail()
            );

            response.setMessage(
                    "Login successful"
            );

            response.setToken(token);

        } else {

            logger.warn(
                    "Failed login attempt for identifier: {}",
                    request.getIdentifier()
            );

            response.setMessage(
                    "Invalid credentials"
            );
        }

        return response;
    }

    public ApiResponse changePassword(
            @Valid ChangePasswordRequest request) {

        logger.info("Password change attempt");

        ApiResponse response =
                new ApiResponse();

        if (request.getCurrentPassword()
                .equals(request.getNewPassword())) {

            logger.warn(
                    "Password change rejected because new password matches current password"
            );

            response.setMessage(
                    "New password cannot be the same as the current password"
            );

            return response;
        }

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()) {

            logger.warn(
                    "Password change attempted without authentication"
            );

            response.setMessage(
                    "Unauthorized"
            );

            return response;
        }

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElse(null);

        if (user == null) {

            logger.warn(
                    "Password change failed. User not found: {}",
                    email
            );

            response.setMessage(
                    "User not found"
            );

            return response;
        }

        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {

            logger.warn(
                    "Password change failed due to incorrect current password for user: {}",
                    email
            );

            response.setMessage(
                    "Current password is incorrect"
            );

            return response;
        }

        user.setPassword(
                passwordEncoder.encode(
                        request.getNewPassword()
                )
        );

        userRepository.save(user);

        logger.info(
                "Password changed successfully for user: {}",
                email
        );

        response.setMessage(
                "Password changed successfully"
        );

        response.setSuccess(true);

        return response;
    }
}