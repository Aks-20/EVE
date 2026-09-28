package com.eve.eve.repository;



import com.eve.eve.entity.DiagnosticTest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticTestRepository
        extends JpaRepository<DiagnosticTest, Long> {
}