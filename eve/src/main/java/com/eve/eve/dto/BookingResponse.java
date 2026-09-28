package com.eve.eve.dto;



import com.eve.eve.entity.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BookingResponse(

        Long id,

        Long centreTestId,

        LocalDateTime appointmentAt,

        BigDecimal amount,

        BookingStatus status
) {
}