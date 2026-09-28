package com.eve.eve.dto;



import jakarta.validation.constraints.NotBlank;

public record CentreRequest(

        @NotBlank
        String name,

        @NotBlank
        String location
) {
}