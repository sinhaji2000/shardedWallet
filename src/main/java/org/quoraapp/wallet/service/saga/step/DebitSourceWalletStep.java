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
@RequiredArgsConstructor
@Service
@Slf4j
public class DebitSourceWalletStep implements SagaStep {

    private final WalletRepository walletRepository ;

    @Override
    @Transactional
    public boolean execute(SagaContext context) {
        
        Long fromWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");
        log.info("debit from source wallet with id {} for amount {}" , fromWalletId , amount);
        Wallet wallet = walletRepository.findByIdWithLock(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Source wallet not found with id: " + fromWalletId));

        log.info("wallet fetch with balance : {}", wallet.getBalance());
        context.put("originalSourceWalletBalance", wallet.getBalance()) ;
        
        // we  already cheaked in wallet entity is suffcient balnace or not so there is no sence to check here again in the step but if you want to check here also you can uncomment the below code
        // if(!wallet.isSufficientBalance(amount)){
        //     throw new RuntimeException("Insufficient balance in source wallet with id: " + fromWalletId);
        // }

        wallet.debit(amount);
        walletRepository.save(wallet) ; // save the updated wallet back to the database
        log.info("Wallet with saved balance {}" , wallet.getBalance());
        context.put("sourceWalletBalanceAfterDebit", wallet.getBalance()) ; // store the balance
                
        return true; // Return true if successful, false otherwise
    }

    @Override
    @Transactional
    public boolean compensate(SagaContext context) {
       
        Long fromWalletId = context.getLong("fromWalletId");
        BigDecimal amount = context.getBigDecimal("amount");


        log.info("compensating DebitSourceWalletStep for wallet id {} and amount {}" , fromWalletId , amount);
        Wallet wallet = walletRepository.findByIdWithLock(fromWalletId)
                .orElseThrow(() -> new RuntimeException("Source wallet not found with id: " + fromWalletId));

        
                
        log.info("wallet fetch with balance : {}", wallet.getBalance());
        context.put("sourceWalletBalanceBeforeCompensation", wallet.getBalance()) ; // store the balance before compensation in the context for reference


        wallet.credit(amount); // compensate by crediting the amount back to the source wallet
        walletRepository.save(wallet) ; // save the updated wallet back to the database
        log.info("Wallet with saved balance {}" , wallet.getBalance());
        context.put("sourceWalletBalanceAfterCompensation", wallet.getBalance()) ; // store the balance after compensation in the context for reference
        
        return true; // Return true if successful, false otherwise
    }

    @Override

    public String getStepName(){

        return "DebitSourceWalletStep";
    }
    
}
