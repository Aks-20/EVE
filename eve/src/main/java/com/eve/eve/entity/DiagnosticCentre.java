package com.eve.eve.entity;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "diagnostic_centres")
public class DiagnosticCentre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 300)
    private String location;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected DiagnosticCentre() {
    }

    public DiagnosticCentre(
            String name,
            String location
    ) {
        this.name = name;
        this.location = location;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
