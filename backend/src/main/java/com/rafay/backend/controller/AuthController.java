package com.rafay.backend.controller;

import com.rafay.backend.dto.request.ChangePasswordRequest;
import com.rafay.backend.dto.request.LoginRequest;
import com.rafay.backend.dto.response.ApiResponse;
import com.rafay.backend.dto.response.LoginResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rafay.backend.dto.request.RegisterRequest;
import com.rafay.backend.dto.response.RegisterResponse;
import com.rafay.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse registerUser(@Valid @RequestBody RegisterRequest request) {
        return authService.registerUser(request);
    }
    @PostMapping("/login")
    public LoginResponse loginUser(@Valid @RequestBody LoginRequest request)
    {
        return authService.loginUser(request);
    }
    @PostMapping("/change")
    public ApiResponse changePassword(@Valid @RequestBody ChangePasswordRequest request)
    {
        return authService.changePassword(request);
    }
}
