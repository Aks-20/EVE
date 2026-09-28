package com.eve.eve.repository;


import com.eve.eve.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBookingId(
            Long bookingId
    );

    Optional<Payment> findByProviderPaymentId(
            String providerPaymentId
    );
}
