package org.quoraapp.wallet.repositories;

import org.quoraapp.wallet.entities.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {
    
}
