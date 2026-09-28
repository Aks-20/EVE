package com.eve.eve.dto;



public record AuthResponse(
        String accessToken,
        String tokenType
) {
}