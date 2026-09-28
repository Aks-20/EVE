package com.eve.eve.dto;



import jakarta.validation.constraints.NotNull;

public record CreatePaymentRequest(

        @NotNull
        Long bookingId
) {
}
