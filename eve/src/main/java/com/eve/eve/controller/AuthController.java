package com.eve.eve.controller;



import com.eve.eve.dto.*;
import com.eve.eve.entity.User;
import com.eve.eve.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse signup(
            @Valid @RequestBody SignupRequest request
    ) {

        User user =
                authService.signup(request);

        return UserResponse.from(user);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {

        String token =
                authService.login(request);

        return new AuthResponse(
                token,
                "Bearer"
        );
    }

    @GetMapping("/me")
    public UserResponse me(
            Authentication authentication
    ) {

        User user =
                (User) authentication.getPrincipal();

        return UserResponse.from(user);
    }
}