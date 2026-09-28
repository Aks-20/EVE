package com.eve.eve.service;



import com.eve.eve.dto.*;
import com.eve.eve.entity.DiagnosticTest;
import com.eve.eve.repository.DiagnosticTestRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiagnosticTestService {

    private final DiagnosticTestRepository repository;

    public DiagnosticTestService(
            DiagnosticTestRepository repository
    ) {
        this.repository = repository;
    }

    public TestResponse create(
            CreateTestRequest request
    ) {

        DiagnosticTest test =
                new DiagnosticTest(
                        request.name(),
                        request.description()
                );

        test = repository.save(test);

        return toResponse(test);
    }

    public List<TestResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TestResponse toResponse(
            DiagnosticTest test
    ) {

        return new TestResponse(
                test.getId(),
                test.getName(),
                test.getDescription()
        );
    }
}