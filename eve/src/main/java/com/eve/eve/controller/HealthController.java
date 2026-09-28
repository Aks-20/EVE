package com.eve.eve.controller;





import com.eve.eve.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/health")
    public ApiResponse<String> health() {

        return new ApiResponse<>(
                true,
                "Service is healthy",
                "EVE Healthcare API"
        );
    }
}