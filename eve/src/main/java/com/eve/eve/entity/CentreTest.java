package com.eve.eve.entity;



import com.eve.eve.entity.DiagnosticTest;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "centre_tests",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_centre_test",
                        columnNames = {"centre_id", "test_id"}
                )
        }
)
public class CentreTest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "centre_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_centre_tests_centre")
    )
    private DiagnosticCentre centre;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "test_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_centre_tests_test")
    )
    private DiagnosticTest test;

    @Column(
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal price;

    protected CentreTest() {
    }

    public CentreTest(
            DiagnosticCentre centre,
            DiagnosticTest test,
            BigDecimal price
    ) {
        this.centre = centre;
        this.test = test;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public DiagnosticCentre getCentre() {
        return centre;
    }

    public void setCentre(DiagnosticCentre centre) {
        this.centre = centre;
    }

    public DiagnosticTest getTest() {
        return test;
    }

    public void setTest(DiagnosticTest test) {
        this.test = test;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}