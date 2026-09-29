package com.eve.eve.service;


import java.util.UUID;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eve.eve.common.Exception.ConflictException;
import com.eve.eve.common.Exception.ResourceNotFoundException;
import com.eve.eve.dto.CreatePaymentRequest;
import com.eve.eve.dto.PaymentResponse;
import com.eve.eve.entity.Booking;
import com.eve.eve.entity.BookingStatus;
import com.eve.eve.entity.Payment;
import com.eve.eve.entity.PaymentStatus;
import com.eve.eve.entity.User;
import com.eve.eve.repository.BookingRepository;
import com.eve.eve.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentService(
            PaymentRepository paymentRepository,
            BookingRepository bookingRepository) {

        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
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
                        if (existing.getStatus() == PaymentStatus.FAILED) {
                                existing.setStatus(PaymentStatus.SUCCESS);
                                booking.setStatus(BookingStatus.CONFIRMED);
                                paymentRepository.save(existing);
                                bookingRepository.save(booking);
                        }
            return toResponse(existing);
        }

        PaymentStatus status = PaymentStatus.SUCCESS;

        String providerPaymentId =
                "pay_" + UUID.randomUUID();

        Payment payment = new Payment(
                booking,
                providerPaymentId,
                booking.getAmount(),
                status
        );

        paymentRepository.save(payment);

        booking.setStatus(BookingStatus.CONFIRMED);

        bookingRepository.save(booking);

        return toResponse(payment);
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