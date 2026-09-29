package com.eve.eve.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eve.eve.dto.CreateTestRequest;
import com.eve.eve.dto.TestResponse;
import com.eve.eve.service.DiagnosticTestService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/tests")
public class DiagnosticTestController {

    private final DiagnosticTestService service;

    public DiagnosticTestController(DiagnosticTestService service) {
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
