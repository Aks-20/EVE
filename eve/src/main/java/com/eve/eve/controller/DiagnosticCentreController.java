package com.eve.eve.controller;



import com.eve.eve.dto.*;
import com.eve.eve.service.DiagnosticCentreService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centres")
public class DiagnosticCentreController {

    private final DiagnosticCentreService service;

    public DiagnosticCentreController(
            DiagnosticCentreService service
    ) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CentreResponse create(
            @Valid @RequestBody CentreRequest request
    ) {

        return service.create(request);
    }

    @GetMapping
    public List<CentreResponse> findAll() {

        return service.findAll();
    }

    @GetMapping("/{id}")
    public CentreResponse findById(
            @PathVariable Long id
    ) {

        return service.findById(id);
    }


    @PostMapping("/{centreId}/tests")
@ResponseStatus(HttpStatus.CREATED)
public CentreTestResponse addTest(
        @PathVariable Long centreId,
        @Valid @RequestBody AddTestToCentreRequest request
) {

    return service.addTest(
            centreId,
            request
    );
}
}
