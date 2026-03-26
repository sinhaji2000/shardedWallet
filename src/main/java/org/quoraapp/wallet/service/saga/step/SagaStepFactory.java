package org.quoraapp.wallet.service.saga.step;

import java.util.Map;

import org.quoraapp.wallet.service.saga.SagaStepInterface;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SagaStepFactory {
    
    private final Map<String , SagaStepInterface> sagaSteps ;
    public static enum SagaStepType {
        DEBIT_SOURCE_WALLET_STEP,
        CREDIT_DESTINATION_WALLET_STEP,
        UPDATE_TRANSACTION_STATUS
    }

    public  SagaStepInterface getSagaStep(String stepName){

        return sagaSteps.get(stepName);
    }
}
