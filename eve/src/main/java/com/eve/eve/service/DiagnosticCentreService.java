package com.eve.eve.service;



import com.eve.eve.dto.*;
import com.eve.eve.entity.DiagnosticCentre;
import com.eve.eve.repository.DiagnosticCentreRepository;
import com.eve.eve.entity.CentreTest;
import com.eve.eve.entity.DiagnosticTest;
import com.eve.eve.repository.CentreTestRepository;
import com.eve.eve.repository.DiagnosticTestRepository;

import com.eve.eve.common.Exception.ConflictException;
import com.eve.eve.common.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticCentreService {

    private final DiagnosticCentreRepository centreRepository;
    private final CentreTestRepository centreTestRepository;
    private final DiagnosticTestRepository testRepository;

    public DiagnosticCentreService(
            DiagnosticCentreRepository centreRepository,
            CentreTestRepository centreTestRepository,
            DiagnosticTestRepository testRepository
    ) {
        this.centreRepository = centreRepository;
        this.centreTestRepository = centreTestRepository;
        this.testRepository = testRepository;
    }

    public CentreResponse create(CentreRequest request) {
        DiagnosticCentre centre = centreRepository.save(
                new DiagnosticCentre(request.name(), request.location())
        );
        return toResponse(centre);
    }

    public List<CentreResponse> findAll() {
        return centreRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public CentreResponse findById(Long id) {
        DiagnosticCentre centre = centreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Diagnostic centre not found: " + id
                ));
        return toResponse(centre);
    }

public CentreTestResponse addTest(
        Long centreId,
        AddTestToCentreRequest request
) {

    DiagnosticCentre centre =
            centreRepository.findById(centreId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Diagnostic centre not found: " + centreId
                            )
                    );

    DiagnosticTest test =
            testRepository.findById(request.testId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Diagnostic test not found"
                            )
                    );

    if (centreTestRepository
            .existsByCentreIdAndTestId(
                    centreId,
                    request.testId()
            )) {

        throw new ConflictException(
                "This test is already available at this centre"
        );
    }

    CentreTest centreTest =
            new CentreTest(
                    centre,
                    test,
                    request.price()
            );

    centreTest =
            centreTestRepository.save(centreTest);

    return new CentreTestResponse(
            centreTest.getId(),
            centre.getId(),
            test.getId(),
            test.getName(),
            centreTest.getPrice()
    );
}

        private CentreResponse toResponse(DiagnosticCentre centre) {
                return new CentreResponse(
                                centre.getId(),
                                centre.getName(),
                                centre.getLocation()
                );
        }
}