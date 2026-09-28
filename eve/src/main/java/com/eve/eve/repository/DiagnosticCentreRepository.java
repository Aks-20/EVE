package com.eve.eve.repository;



import com.eve.eve.entity.DiagnosticCentre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiagnosticCentreRepository
        extends JpaRepository<DiagnosticCentre, Long> {
}