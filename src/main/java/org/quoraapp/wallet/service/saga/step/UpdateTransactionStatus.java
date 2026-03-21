package org.quoraapp.wallet.service.saga.step;

import org.quoraapp.wallet.entities.Transaction;
import org.quoraapp.wallet.entities.TransactionStatus;
import org.quoraapp.wallet.repositories.TransactionalReposotory;
import org.quoraapp.wallet.service.saga.SagaContext;
import org.quoraapp.wallet.service.saga.SagaStep;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor

@Slf4j
public class UpdateTransactionStatus implements SagaStep {

    private final TransactionalReposotory transactionalReposotory;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        // Implement logic to update the transaction status in the database
        // For example, retrieve the transaction id from the context and update its status to "COMPLETED"
        Long transactionId = context.getLong("transactionId");
        log.info("Updating transaction status for transaction id: {}", transactionId);

        Transaction transaction = transactionalReposotory.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));
                        
        context.put("OriginalTransactionStatus", transaction.getStatus()); // Store original status for compensation
        transaction.setStatus(TransactionStatus.SUCCESS);

        transactionalReposotory.save(transaction);

        log.info("Transaction status updated to SUCCESS for transaction id: {}", transactionId);
        context.put("TransactionStatusAfterUpdate", transaction.getStatus()); // Store updated status for compensation
        log.info("Updated transaction status executed successfully: {}", transaction.getStatus());

        return true; // Return true if successful, false otherwise
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
        // Implement logic to revert the transaction status update in case of compensation
        // For example, retrieve the transaction id from the context and update its status back to "PENDING"

        Long transactionId = context.getLong("transactionId");
        TransactionStatus originalStatus = TransactionStatus.valueOf(context.getString("OriginalTransactionStatus"));
        log.info("Compensating transaction status update for transaction id: {}", transactionId);
        Transaction transaction = transactionalReposotory.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + transactionId));


        transaction.setStatus(originalStatus); // Revert to original status as needed

        transactionalReposotory.save(transaction); // Save the reverted transaction status

        log.info("Transaction status reverted to {} for transaction id: {}", originalStatus, transactionId);

        return true; // Return true if successful, false otherwise
    }

    @Override
    public String getStepName() {
        return "UpdateTransactionStatus";   
    }
    

}
