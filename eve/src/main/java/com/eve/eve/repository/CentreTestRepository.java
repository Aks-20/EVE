package com.eve.eve.repository;



import com.eve.eve.entity.CentreTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CentreTestRepository
        extends JpaRepository<CentreTest, Long> {

    boolean existsByCentreIdAndTestId(
            Long centreId,
            Long testId
    );

    Optional<CentreTest> findByCentreIdAndTestId(
            Long centreId,
            Long testId
    );
}
