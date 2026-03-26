package org.quoraapp.wallet.repositories;

import java.util.List;

import org.quoraapp.wallet.entities.SagaInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SagaInstanceRepository extends JpaRepository<SagaInstance, Long> {

}
