package com.eve.eve.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.eve.eve.entity.User;
import com.eve.eve.repository.UserRepository;

@Component
public class AdminAccountInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String name;
    private final String email;
    private final String password;

    public AdminAccountInitializer(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.name:}") String name,
            @Value("${app.admin.email:}") String email,
            @Value("${app.admin.password:}") String password
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (name.isBlank() && email.isBlank() && password.isBlank()) {
            return;
        }

        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            throw new IllegalStateException(
                    "ADMIN_NAME, ADMIN_EMAIL, and ADMIN_PASSWORD must all be configured"
            );
        }

        User admin = userRepository.findByEmail(email)
                .orElseGet(() -> new User(
                        name,
                        email,
                        passwordEncoder.encode(password)
                ));

        if (!passwordEncoder.matches(password, admin.getPasswordHash())) {
            throw new IllegalStateException(
                    "Configured admin email already exists with a different password"
            );
        }

        admin.setRole("ADMIN");
        userRepository.save(admin);
    }
}