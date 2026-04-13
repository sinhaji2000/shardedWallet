package org.quoraapp.wallet.entities;

import java.math.BigDecimal;

import groovy.transform.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Entity
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    @Column(name = "from_wallet_id" , nullable = false)
    private Long fromWalletId ;

    @Column(name = "to_wallet_id" , nullable = false)
    private Long toWalletId ;

    @Column(name = "amount" , nullable = false)
    private BigDecimal amount ;

    

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransactionStatus status = TransactionStatus.PENDING; // PENDING, COMPLETED, FAILED

    @Enumerated(EnumType.STRING)
    @Column(name = "transactional_type")
    private TransactionType type = TransactionType.TRANSFER; // DEBIT, DEPOSIT, WITHDRAWAL

    @Column(name = "description")
    private String description;

    @Column(name = "saga_instance_id")
    private Long sagaInstanceId;
}
