package com.eve.eve.service;


import org.springframework.transaction.annotation.Transactional;
import com.eve.eve.dto.CreatePaymentRequest;
import com.eve.eve.dto.PaymentResponse;
import com.eve.eve.dto.PaymentWebhookRequest;
import com.eve.eve.entity.User;
import com.eve.eve.entity.Booking;
import com.eve.eve.entity.BookingStatus;
import com.eve.eve.repository.BookingRepository;

import com.eve.eve.entity.Payment;
import com.eve.eve.entity.PaymentStatus;
import com.eve.eve.repository.PaymentRepository;
import com.eve.eve.repository.WebhookEventRepository;
import com.eve.eve.common.Exception.BadRequestException;
import com.eve.eve.common.Exception.ConflictException;
import com.eve.eve.common.Exception.ResourceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.access.AccessDeniedException;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final WebhookEventRepository webhookEventRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository,
            WebhookEventRepository webhookEventRepository,
            ObjectMapper objectMapper) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.webhookEventRepository = webhookEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public PaymentResponse processPayment(
            User user,
            CreatePaymentRequest request) {

        Booking booking = bookingRepository.findByIdForUpdate(request.bookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + request.bookingId()
                        ));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException(
                    "You are not allowed to pay for this booking"
            );
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new ConflictException(
                    "Payment cannot be processed for booking in status "
                            + booking.getStatus()
            );
        }

        Payment existing = paymentRepository
                .findByBookingId(booking.getId())
                .orElse(null);

        if (existing != null) {
            return toResponse(existing);
        }

        boolean success = Math.random() < 0.8;

        PaymentStatus status = success
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;

        String providerPaymentId =
                "pay_" + UUID.randomUUID();

        Payment payment = new Payment(
                booking,
                providerPaymentId,
                booking.getAmount(),
                status
        );

        paymentRepository.save(payment);

        booking.setStatus(
                success
                        ? BookingStatus.CONFIRMED
                        : BookingStatus.FAILED
        );

        bookingRepository.save(booking);

        return toResponse(payment);
    }

    @Transactional
    public void processWebhook(
            PaymentWebhookRequest request) {

        String payload;

        try {
            payload = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid webhook payload");
        }

        int inserted = webhookEventRepository.insertIfNew(
                request.eventId(),
                request.eventType(),
                payload
        );

        if (inserted == 0) {
            // Already processed.
            return;
        }

        Booking booking = bookingRepository
                .findByIdForUpdate(request.bookingId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Booking not found: " + request.bookingId()
                        ));

        Payment payment = paymentRepository
                .findByProviderPaymentId(request.paymentId())
                .orElse(null);

        if (payment != null &&
                !payment.getBooking().getId().equals(booking.getId())) {

            throw new ConflictException(
                    "Payment does not belong to this booking"
            );
        }

        if (payment == null) {
            payment = new Payment(
                    booking,
                    request.paymentId(),
                    booking.getAmount(),
                    request.status()
            );

            paymentRepository.save(payment);
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException(
                    "Cannot update payment for cancelled booking"
            );
        }

        if (request.status() == PaymentStatus.SUCCESS) {
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            booking.setStatus(BookingStatus.FAILED);
        }

        bookingRepository.save(booking);
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getProviderPaymentId(),
                payment.getAmount(),
                payment.getStatus()
        );
    }
}