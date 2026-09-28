package com.eve.eve.controller;



import com.eve.eve.dto.CreatePaymentRequest;
import com.eve.eve.dto.PaymentResponse;
import com.eve.eve.dto.PaymentWebhookRequest;
import com.eve.eve.common.ApiResponse;
import com.eve.eve.entity.User;
import com.eve.eve.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
        private final String webhookSecret;

    public PaymentController(
                        PaymentService paymentService,
                        @Value("${webhook.secret}") String webhookSecret
    ) {
        this.paymentService = paymentService;
                this.webhookSecret = webhookSecret;
    }

    @PostMapping
    public PaymentResponse processPayment(
            @Valid @RequestBody CreatePaymentRequest request,
            Authentication authentication
    ) {

        User user =
                (User) authentication.getPrincipal();

        return paymentService.processPayment(
                user,
                request
        );
    }

  @PostMapping("/webhook")
public ResponseEntity<ApiResponse<Void>> webhook(
        @RequestHeader("X-Webhook-Secret") String secret,
        @Valid @RequestBody PaymentWebhookRequest request) {

    if (!webhookSecret.equals(secret)) {
        throw new AccessDeniedException("Invalid webhook secret");
    }

    paymentService.processWebhook(request);

    return ResponseEntity.ok(
            new ApiResponse<>(
                    true,
                    "Webhook processed",
                    null
            )
    );
}
}