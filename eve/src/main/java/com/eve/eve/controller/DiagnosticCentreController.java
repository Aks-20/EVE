package com.eve.eve.controller;



import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.eve.eve.dto.AddTestToCentreRequest;
import com.eve.eve.dto.CentreRequest;
import com.eve.eve.dto.CentreResponse;
import com.eve.eve.dto.CentreTestResponse;
import com.eve.eve.service.DiagnosticCentreService;

import jakarta.validation.Valid;

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
