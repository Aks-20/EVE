package com.eve.eve.dto;



import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateBookingRequest(

        @NotNull
        Long centreTestId,

        @NotNull
        @Future
        LocalDateTime appointmentAt
) {
}