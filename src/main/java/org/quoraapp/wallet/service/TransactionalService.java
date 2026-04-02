package org.quoraapp.wallet.service;

import java.math.BigDecimal;
import java.util.List;

import org.quoraapp.wallet.entities.Transaction;
import org.quoraapp.wallet.entities.TransactionStatus;
import org.quoraapp.wallet.repositories.TransactionalReposotory;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionalService {
    
    private final TransactionalReposotory transactionalReposotory;

    @Transactional
    public Transaction createTransaction(Long fromWalletId , Long toWalletId , BigDecimal amaount ,String description ){

        log.info("Creating transaction from wallet {} to wallet {} for amount {}", fromWalletId, toWalletId, amaount);
        Transaction transaction = Transaction.builder()
                .fromWalletId(fromWalletId)
                .toWalletId(toWalletId)
                .amount(amaount)
                .description(description)
                .build();

        Transaction savedTransaction =  transactionalReposotory.save(transaction);
        log.info("Transaction created with id: {}", savedTransaction.getId());
        return savedTransaction;
    }

    public Transaction getTransactionById(Long id){
        return transactionalReposotory.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
    }

    public List<Transaction> getTransactionByWalletId(Long walletId){
        return transactionalReposotory.findByWalletId(walletId) ;
    }

    public List<Transaction>getTransactionByFromWalletId(Long fromWalletId){
        return transactionalReposotory.findByFromWalletId(fromWalletId);
    }

    public List<Transaction>getTransactionsByToWalletId(Long toWalletId){
        return transactionalReposotory.findByToWalletId(toWalletId);
    }   

    public List<Transaction> getTransactionBySagaInstanceId(Long sagaInstanceId){
        return transactionalReposotory.findBySagaInstanceId(sagaInstanceId);
    }

    public List<Transaction> getTransactionByStatus(TransactionStatus status){
        return transactionalReposotory.findByStatus(status);
    }
}
