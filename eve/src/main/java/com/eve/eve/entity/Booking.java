package com.eve.eve.entity;

import com.eve.eve.entity.User;
import com.eve.eve.entity.CentreTest;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "centre_test_id",
            nullable = false
    )
    private CentreTest centreTest;

    @Column(
            name = "appointment_at",
            nullable = false
    )
    private LocalDateTime appointmentAt;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    protected Booking() {
    }

    public Booking(
            User user,
            CentreTest centreTest,
            LocalDateTime appointmentAt,
            BigDecimal amount
    ) {

        this.user = user;
        this.centreTest = centreTest;
        this.appointmentAt = appointmentAt;
        this.amount = amount;
        this.status = BookingStatus.PENDING;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public CentreTest getCentreTest() {
        return centreTest;
    }

    public LocalDateTime getAppointmentAt() {
        return appointmentAt;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(
            BookingStatus status
    ) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}