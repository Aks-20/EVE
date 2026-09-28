package com.eve.eve.controller;



import com.eve.eve.dto.*;
import com.eve.eve.service.DiagnosticTestService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tests")
public class DiagnosticTestController {

    private final DiagnosticTestService service;

    public DiagnosticTestController(
            DiagnosticTestService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TestResponse create(
            @Valid @RequestBody CreateTestRequest request
    ) {

        return service.create(request);
    }

    @GetMapping
    public List<TestResponse> findAll() {

        return service.findAll();
    }
}