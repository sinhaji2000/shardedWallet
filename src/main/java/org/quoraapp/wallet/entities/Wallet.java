package org.quoraapp.wallet.entities;

import java.math.BigDecimal;

import groovy.transform.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "wallet")
public class Wallet {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id" , nullable = false)
    private Long userId;

    @Column(name = "is_active" , nullable = false)
    private Boolean isActive;
   
    
    @Column(name = "balance" , nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    public boolean isSufficientBalance(BigDecimal amount) {
        return balance.compareTo(amount) >= 0;
    }

    public void debit(BigDecimal amount) {
        if (isSufficientBalance(amount)) {
            balance = balance.subtract(amount);
        } else {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }
}
