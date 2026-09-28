package com.eve.eve.controller;


import com.eve.eve.entity.User;
import com.eve.eve.dto.*;
import com.eve.eve.service.BookingService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(
            BookingService bookingService
    ) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse create(
            @Valid @RequestBody CreateBookingRequest request,
            Authentication authentication
    ) {

        User user =
                (User) authentication.getPrincipal();

        return bookingService.create(
                user,
                request
        );
    }

    @GetMapping
    public List<BookingResponse> findMine(
            Authentication authentication
    ) {

        User user =
                (User) authentication.getPrincipal();

        return bookingService.findMyBookings(user);
    }

    @GetMapping("/{id}")
    public BookingResponse findById(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User user =
                (User) authentication.getPrincipal();

        return bookingService.findById(
                id,
                user
        );
    }

    @PostMapping("/{id}/cancel")
    public BookingResponse cancel(
            @PathVariable Long id,
            Authentication authentication
    ) {

        User user =
                (User) authentication.getPrincipal();

        return bookingService.cancel(
                id,
                user
        );
    }
}