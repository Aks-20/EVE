package com.eve.eve.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.eve.eve.dto.CreatePaymentRequest;
import com.eve.eve.dto.PaymentResponse;
import com.eve.eve.entity.Booking;
import com.eve.eve.entity.BookingStatus;
import com.eve.eve.entity.Payment;
import com.eve.eve.entity.PaymentStatus;
import com.eve.eve.entity.User;
import com.eve.eve.repository.BookingRepository;
import com.eve.eve.repository.PaymentRepository;

class PaymentServiceTest {

    @Test
    void processPaymentSucceedsAndConfirmsBooking() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        BookingRepository bookingRepository = mock(BookingRepository.class);
        PaymentService paymentService = new PaymentService(
                paymentRepository,
                bookingRepository
        );

        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);

        Booking booking = mock(Booking.class);
        when(bookingRepository.findByIdForUpdate(10L))
                .thenReturn(Optional.of(booking));
        when(booking.getUser()).thenReturn(user);
        when(booking.getId()).thenReturn(10L);
        when(booking.getAmount()).thenReturn(new BigDecimal("350.00"));
        when(booking.getStatus()).thenReturn(BookingStatus.PENDING);
        when(paymentRepository.findByBookingId(10L)).thenReturn(Optional.empty());

        PaymentResponse response = paymentService.processPayment(
                user,
                new CreatePaymentRequest(10L)
        );

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertEquals(PaymentStatus.SUCCESS, paymentCaptor.getValue().getStatus());
        assertNotNull(response);
        verify(booking).setStatus(BookingStatus.CONFIRMED);
        verify(bookingRepository).save(booking);
    }

    @Test
    void retryConvertsPreviouslyFailedSimulationToSuccess() {
        PaymentRepository paymentRepository = mock(PaymentRepository.class);
        BookingRepository bookingRepository = mock(BookingRepository.class);
        PaymentService paymentService = new PaymentService(
                paymentRepository,
                bookingRepository
        );

        User user = mock(User.class);
        when(user.getId()).thenReturn(7L);

        Booking booking = mock(Booking.class);
        when(bookingRepository.findByIdForUpdate(10L))
                .thenReturn(Optional.of(booking));
        when(booking.getUser()).thenReturn(user);
        when(booking.getId()).thenReturn(10L);
        when(booking.getAmount()).thenReturn(new BigDecimal("350.00"));
        when(booking.getStatus()).thenReturn(BookingStatus.PENDING);

        Payment existing = new Payment(
                booking,
                "pay_existing",
                new BigDecimal("350.00"),
                PaymentStatus.FAILED
        );
        when(paymentRepository.findByBookingId(10L)).thenReturn(Optional.of(existing));

        PaymentResponse response = paymentService.processPayment(
                user,
                new CreatePaymentRequest(10L)
        );

        assertEquals(PaymentStatus.SUCCESS, existing.getStatus());
        assertEquals(PaymentStatus.SUCCESS, response.status());
        verify(paymentRepository).save(existing);
        verify(booking).setStatus(BookingStatus.CONFIRMED);
        verify(bookingRepository).save(booking);
    }
}