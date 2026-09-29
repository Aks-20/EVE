package com.eve.eve.controller;



import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eve.eve.dto.CreatePaymentRequest;
import com.eve.eve.dto.PaymentResponse;
import com.eve.eve.entity.User;
import com.eve.eve.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

        public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
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

}