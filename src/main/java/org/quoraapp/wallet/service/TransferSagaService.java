package org.quoraapp.wallet.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.quoraapp.wallet.entities.Transaction;
import org.quoraapp.wallet.service.saga.SagaContext;
import org.quoraapp.wallet.service.saga.step.SagaOrchestrator;
import org.quoraapp.wallet.service.saga.step.SagaStepFactory;
import org.quoraapp.wallet.service.saga.step.SagaStepFactory.SagaStepType;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferSagaService {
    
    private final TransactionalService transactionalService;
    private final SagaOrchestrator sagaOrchestrator;

    

    @Transactional
    public Long initiateTransfer(Long fromWalletId , Long toWalletId , BigDecimal amount , String description){
       
        log.info("Initiating transfer from wallet {} to wallet {} for amount {}", fromWalletId, toWalletId, amount);

        Transaction transaction = transactionalService.createTransaction(fromWalletId, toWalletId, amount, description);

        SagaContext sagaContext = SagaContext.builder()
                .data(Map.ofEntries(
                    Map.entry("transactionId", transaction.getId()),
                    Map.entry("fromWalletId", fromWalletId),
                    Map.entry("toWalletId", toWalletId),
                    Map.entry("amount", amount),
                    Map.entry("description", description)
                ))
                .build();

        Long sagaInstanceId = sagaOrchestrator.startSaga(sagaContext);
        log.info("Saga initiated with instance ID: {}", sagaInstanceId);
       
        transactionalService.updateTransactionWithSagaInstanceId(transaction.getId(), sagaInstanceId);
        executeTransferSaga(sagaInstanceId);
        return sagaInstanceId;

    }

    public void executeTransferSaga(Long sagaInstanceId){
        
        log.info("Executing transfer saga with instance ID: {}", sagaInstanceId);

        try{

            for(SagaStepType step : SagaStepFactory.TransferMoneySagaSteps){
                boolean succes = sagaOrchestrator.executeStep(sagaInstanceId, step.name());

                if(!succes){
                    log.error("Saga step {} failed for instance ID: {}", step.name(), sagaInstanceId);
                    sagaOrchestrator.failSaga(sagaInstanceId);
                    return;
                }
            }

           
            sagaOrchestrator.completeSaga(sagaInstanceId);
            log.info("Transfer saga with instance ID: {} completed successfully", sagaInstanceId);

        }catch(Exception e){
            log.error("Error executing transfer saga with instance ID: {}", sagaInstanceId);
            sagaOrchestrator.failSaga(sagaInstanceId);
        }
    }
}
