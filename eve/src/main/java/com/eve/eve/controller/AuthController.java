package com.eve.eve.controller;



import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eve.eve.dto.AuthResponse;
import com.eve.eve.dto.LoginRequest;
import com.eve.eve.dto.SignupRequest;
import com.eve.eve.dto.UserResponse;
import com.eve.eve.entity.User;
import com.eve.eve.service.AuthService;

import jakarta.validation.Valid;

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
    System.out.println("1. Signup request received");

    User user = authService.signup(request);

    System.out.println("2. User saved: " + user.getId());

    UserResponse response = UserResponse.from(user);

    System.out.println("3. UserResponse created");

    return response;
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