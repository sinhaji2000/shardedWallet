package org.quoraapp.wallet.repositories;

import java.util.List;
import java.util.Optional;

import org.quoraapp.wallet.entities.SagaStep;
import org.quoraapp.wallet.entities.StepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SagaStepRepository extends JpaRepository<SagaStep, Long> {
    
    List<SagaStep> findBySagaInstanceId(Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaInstanceId AND s.status = 'PENDING'")
    List<SagaStep>findPendingSagaStepsBySagaInstanceId(Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaInstanceId AND s.status = 'COMPLETED'")
    List<SagaStep>findCompletedSagaStepsBySagaInstanceId(@Param("sagaInstanceId")  Long sagaInstanceId);

    @Query("SELECT s FROM SagaStep s WHERE s.sagaInstance.id = :sagaInstanceId AND s.status IN ('COMPLETED', 'COMPENSATED')")
    List<SagaStep>findCompletedOrCompensatedSagaStepsBySagaInstanceId(@Param("sagaInstanceId")  Long sagaInstanceId);

    List<SagaStep> findBySagaInstanceIdAndStatus(Long sagaInstanceId, StepStatus status);

    Optional<SagaStep> findBySagaInstanceIdAndStepNameAndStatus(Long sagaInstanceId, String stepName,
            StepStatus status);
}
