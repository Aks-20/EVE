package com.eve.eve.dto;

import com.eve.eve.entity.PaymentStatus;

import java.math.BigDecimal;

public record PaymentResponse(
        Long id,
        Long bookingId,
        String providerPaymentId,
        BigDecimal amount,
        PaymentStatus status
) {
}