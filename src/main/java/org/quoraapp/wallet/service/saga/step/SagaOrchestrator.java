package org.quoraapp.wallet.service.saga.step;

import org.quoraapp.wallet.entities.SagaInstance;
import org.quoraapp.wallet.service.saga.SagaContext;

public interface SagaOrchestrator {
    
    Long startSaga(SagaContext context);

    boolean executeStep(Long sagaInstanceId, String stepName);

    boolean compensateStep(Long sagaInstanceId, String stepName);

    SagaInstance getSagaInstance(Long sagaInstanceId);

    void compensateSaga(Long sagaInstanceId) ;

    void completeSaga(Long sagaInstanceId) ;
    void failSaga(Long sagaInstanceId) ;
}
