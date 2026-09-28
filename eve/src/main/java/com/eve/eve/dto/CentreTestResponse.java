package com.eve.eve.dto;



import java.math.BigDecimal;

public record CentreTestResponse(

        Long id,

        Long centreId,

        Long testId,

        String testName,

        BigDecimal price
) {
}
