package com.eve.eve.entity;


import com.eve.eve.entity.Booking;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "booking_id",
            nullable = false,
            unique = true
    )
    private Booking booking;

    @Column(
            name = "provider_payment_id",
            nullable = false,
            unique = true
    )
    private String providerPaymentId;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    protected Payment() {
    }

    public Payment(
            Booking booking,
            String providerPaymentId,
            BigDecimal amount,
            PaymentStatus status
    ) {

        this.booking = booking;
        this.providerPaymentId = providerPaymentId;
        this.amount = amount;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Booking getBooking() {
        return booking;
    }

    public String getProviderPaymentId() {
        return providerPaymentId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}