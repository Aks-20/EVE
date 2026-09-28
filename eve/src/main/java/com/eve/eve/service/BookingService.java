package com.eve.eve.service;


import com.eve.eve.entity.User;
import com.eve.eve.dto.*;
import com.eve.eve.entity.Booking;
import com.eve.eve.entity.BookingStatus;
import com.eve.eve.repository.BookingRepository;
import com.eve.eve.entity.CentreTest;
import com.eve.eve.repository.CentreTestRepository;
import com.eve.eve.common.Exception.BadRequestException;
import com.eve.eve.common.Exception.ConflictException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;

    private final CentreTestRepository centreTestRepository;

    public BookingService(
            BookingRepository bookingRepository,
            CentreTestRepository centreTestRepository
    ) {

        this.bookingRepository = bookingRepository;
        this.centreTestRepository = centreTestRepository;
    }

    public BookingResponse create(
            User user,
            CreateBookingRequest request
    ) {

        CentreTest centreTest =
                centreTestRepository
                        .findById(request.centreTestId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Centre test not found"
                                )
                        );

        Booking booking =
                new Booking(
                        user,
                        centreTest,
                        request.appointmentAt(),
                        centreTest.getPrice()
                );

        booking =
                bookingRepository.save(booking);

        return toResponse(booking);
    }

    public List<BookingResponse> findMyBookings(
            User user
    ) {

        return bookingRepository
                .findByUserId(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse findById(
            Long id,
            User user
    ) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Booking not found"
                                )
                        );

        if (!booking.getUser()
                .getId()
                .equals(user.getId())) {

            throw new BadRequestException(
                    "You are not allowed to access this booking"
            );
        }

        return toResponse(booking);
    }

    public BookingResponse cancel(
            Long id,
            User user
    ) {

        Booking booking =
                bookingRepository.findById(id)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Booking not found"
                                )
                        );

        if (!booking.getUser()
                .getId()
                .equals(user.getId())) {

            throw new AccessDeniedException(
                    "You are not allowed to access this booking"
            );
        }

        if (booking.getStatus()
                != BookingStatus.PENDING) {

            throw new ConflictException(
                    "Only pending bookings can be cancelled"
            );
        }

        booking.setStatus(
                BookingStatus.CANCELLED
        );

        return toResponse(
                bookingRepository.save(booking)
        );
    }

    private BookingResponse toResponse(
            Booking booking
    ) {

        return new BookingResponse(
                booking.getId(),
                booking.getCentreTest().getId(),
                booking.getAppointmentAt(),
                booking.getAmount(),
                booking.getStatus()
        );
    }
}
