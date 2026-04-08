package org.quoraapp.wallet.repositories;

import java.util.List;
import java.util.Optional;

import org.quoraapp.wallet.entities.SagaStep;
import org.quoraapp.wallet.entities.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {
    
    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    // @Query("SELECT s FROM SagaStep s WHERE s.sagaInstanceId = :sagaInstanceId AND
    // s.status = 'PENDING'")
    // List<SagaStep> findPendingSagaStepsBySagaInstanceId(@Param("sagaInstanceId")
    // Long sagaInstanceId);



    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaInstanceId, StepStatus status);

    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(Long sagaInstanceId, String stepName,
            StepStatus status);
}

