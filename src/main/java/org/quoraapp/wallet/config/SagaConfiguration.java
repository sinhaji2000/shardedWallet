package org.quoraapp.wallet.config;

import java.util.Map;

import org.quoraapp.wallet.service.saga.SagaStepInterface;
import org.quoraapp.wallet.service.saga.step.CreditDestinationWalletStep;
import org.quoraapp.wallet.service.saga.step.UpdateTransactionStatusStep;
import org.quoraapp.wallet.service.saga.step.DebitSourceWalletStep;
import org.quoraapp.wallet.service.saga.step.SagaStepFactory.SagaStepType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SagaConfiguration {
    
    @Bean
    public Map<String , SagaStepInterface>sagaSteps(
        DebitSourceWalletStep debitSourceWalletStep ,
        CreditDestinationWalletStep creditDestinationWalletStep,
        UpdateTransactionStatusStep updateTransactionStatusStep
    ){
        Map<String , SagaStepInterface> sagaStepMap = new java.util.HashMap<>();
        sagaStepMap.put(SagaStepType.DEBIT_SOURCE_WALLET_STEP.toString(), debitSourceWalletStep);
        sagaStepMap.put(SagaStepType.CREDIT_DESTINATION_WALLET_STEP.toString(), creditDestinationWalletStep);
        sagaStepMap.put(SagaStepType.UPDATE_TRANSACTION_STATUS.toString(), updateTransactionStatusStep);

        return sagaStepMap ;
    }
}
