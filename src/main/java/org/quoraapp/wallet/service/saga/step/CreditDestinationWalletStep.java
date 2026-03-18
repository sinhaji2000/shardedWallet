package org.quoraapp.wallet.service.saga.step;

import java.math.BigDecimal;

import org.quoraapp.wallet.entities.Wallet;
import org.quoraapp.wallet.repositories.WalletRepository;
import org.quoraapp.wallet.service.saga.SagaContext;
import org.quoraapp.wallet.service.saga.SagaStep;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditDestinationWalletStep implements SagaStep{
    
    private final WalletRepository walletRepository ;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        log.info("Executing CreditDestinationWalletStep");
        // Implement logic to credit the destination wallet
        // For example, retrieve the destination wallet and update its balance

        // step 1 :- get the destination wallet id from the context
        Long toWalletId = context.getLong("toWalletId");

        BigDecimal amount = context.getBigDecimal("amount");
        log.info("CreditDestinationWalletStep - toWalletId: {}, amount: {}", toWalletId, amount);


        // step 2 :- fetch the destination wallet form the database with a lock to prevent concurrent updates
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found with id: " + toWalletId));

        log.info("wallet fetch with balance : {}", wallet.getBalance());
        context.put("destinationWalletBalanceBefore", wallet.getBalance()) ; // store the balance before the update in the context for compensation if needed
        
        

         // step 3 :- credit the amount to the destination wallet
         wallet.credit(amount);
            walletRepository.save(wallet) ; // save the updated wallet back to the database
        log.info("Wallet with saved balance {}" , wallet.getBalance());
        context.put("destinationWalletBalanceAfterCredit", wallet.getBalance()) ; // store the balance after the update in the context for compensation if needed

       
        return true; // Return true if successful, false otherwise      
    }

    @Override
    public boolean compensate(SagaContext context) {

        // step 1 :- get the destination wallet id from the context
        Long toWalletId = context.getLong("toWalletId");

        BigDecimal amount = context.getBigDecimal("amount");
        log.info("Compensating CreditDestinationWalletStep - toWalletId: {}, amount: {}", toWalletId, amount);

        // step 2 :- fetch the destination wallet form the database with a lock to
        // prevent concurrent updates
        Wallet wallet = walletRepository.findByIdWithLock(toWalletId)
                .orElseThrow(() -> new RuntimeException("Destination wallet not found with id: " + toWalletId));

        log.info("wallet fetch with balance : {}", wallet.getBalance());

        // step 3 :- credit the amount to the destination wallet
        wallet.debit(amount);
        walletRepository.save(wallet); // save the updated wallet back to the database
        log.info("Wallet with saved balance {}", wallet.getBalance());
        context.put("destinationWalletBalanceAfterCompensation", wallet.getBalance()); // store the balance after the
                                                                                       // update in the context for
                                                                                       // compensation if needed

        log.info("Compensation completed for CreditDestinationWalletStep for wallet id: {}", toWalletId);
        return true; // Return true if successful, false otherwise
    }

    @Override
    public String getStepName() {
        return "CreditDestinationWalletStep";
    }




    
}
