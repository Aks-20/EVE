package com.eve.eve.service;






import com.eve.eve.dto.LoginRequest;
import com.eve.eve.dto.SignupRequest;
import com.eve.eve.entity.User;
import com.eve.eve.repository.UserRepository;
import com.eve.eve.config.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public User signup(
            SignupRequest request
    ) {

        if (userRepository.existsByEmail(
                request.email()
        )) {

            throw new IllegalArgumentException(
                    "Email already registered"
            );
        }

        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(
                        request.password()
                )
        );

        return userRepository.save(user);
    }

    public String login(
            LoginRequest request
    ) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {

            throw new IllegalArgumentException(
                    "Invalid email or password"
            );
        }

        return jwtService.generateToken(
                user.getId()
        );
    }
}