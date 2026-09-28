package com.eve.eve.dto;



import com.eve.eve.entity.PaymentStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PaymentWebhookRequest(

        @NotBlank
        String eventId,

        @NotBlank
        String eventType,

        @NotBlank
        String paymentId,

        @NotNull
        Long bookingId,

        @NotNull
        PaymentStatus status
) {
}