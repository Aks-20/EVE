package com.eve.eve.dto;



import jakarta.validation.constraints.NotBlank;

public record CreateTestRequest(

        @NotBlank
        String name,

        String description
) {
}
