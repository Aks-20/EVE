package com.eve.eve.config;



import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.eve.eve.entity.User;
import com.eve.eve.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization =
                request.getHeader("Authorization");

        if (authorization == null
                || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authorization.substring(7);

        if (!jwtService.isValid(token)) {

            filterChain.doFilter(request, response);
            return;
        }

        try {

            Long userId =
                    jwtService.extractUserId(token);

            User user =
                    userRepository.findById(userId)
                            .orElse(null);

            if (user != null
                    && SecurityContextHolder
                    .getContext()
                    .getAuthentication() == null) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + user.getRole()
                                        )
                                )
                        );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );
            }

        } catch (Exception ignored) {
            // Invalid token → request remains unauthenticated
        }

        filterChain.doFilter(request, response);
    }
}