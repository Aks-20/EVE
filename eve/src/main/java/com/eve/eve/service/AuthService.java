package com.eve.eve.service;






import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.eve.eve.common.Exception.BadRequestException;
import com.eve.eve.common.Exception.ConflictException;
import com.eve.eve.config.JwtService;
import com.eve.eve.dto.LoginRequest;
import com.eve.eve.dto.SignupRequest;
import com.eve.eve.entity.User;
import com.eve.eve.repository.UserRepository;

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

   public User signup(SignupRequest request) {

    System.out.println("A. Checking email");

    if (userRepository.existsByEmail(request.email())) {
        throw new ConflictException("Email already registered");
    }

    System.out.println("B. Email available");

    String encodedPassword =
            passwordEncoder.encode(request.password());

    System.out.println("C. Password encoded");

    User user = new User(
            request.name(),
            request.email(),
            encodedPassword
    );

    System.out.println("D. User entity created");

    User savedUser = userRepository.save(user);

    System.out.println("E. User saved with ID: " + savedUser.getId());

    return savedUser;
}
    public String login(
            LoginRequest request
    ) {

        User user = userRepository
                .findByEmail(request.email())
                .orElseThrow(() ->
                        new BadRequestException(
                                "Invalid email or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPasswordHash()
        )) {

            throw new BadRequestException(
                    "Invalid email or password"
            );
        }

        return jwtService.generateToken(
                user.getId()
        );
    }
}