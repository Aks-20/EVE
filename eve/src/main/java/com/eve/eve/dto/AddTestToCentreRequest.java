package com.eve.eve.dto;



import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddTestToCentreRequest(

        @NotNull
        Long testId,

        @NotNull
        @DecimalMin(value = "0.01")
        BigDecimal price
) {
}
