package org.quoraapp.wallet.service;

import java.math.BigDecimal;
import java.util.List;

import org.quoraapp.wallet.entities.Wallet;
import org.quoraapp.wallet.repositories.WalletRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    
    private final WalletRepository walletRepository;

    public Wallet createWallet(Long userId){
        log.info("Creating wallet for user: {}", userId);
        Wallet wallet = Wallet.builder()
                .userId(userId)
                .isActive(true)
                .balance(BigDecimal.ZERO)
                .build();
        wallet =  walletRepository.save(wallet);
        log.info("Wallet created with id: {}", wallet.getId());
        return wallet;
    }

    public Wallet getWalletById(Long id){
        return walletRepository.findById(id).orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
    }

    public List<Wallet>getWalletByUserId(Long userId){
        return walletRepository.findByUserId(userId);
    }

    @Transactional
    public void debit(Long walletId , BigDecimal amount){
        log.info("Debiting amount {} from wallet {}", amount, walletId);
        Wallet wallet = getWalletById(walletId);
        wallet.debit(amount);
        walletRepository.save(wallet);
        log.info("Amount {} debited from wallet {}", amount, walletId);
    }

    @Transactional
    public void credit(Long walletId , BigDecimal amount){
        log.info("Crediting amount {} to wallet {}", amount, walletId);
        Wallet wallet = getWalletById(walletId);
        wallet.credit(amount);
        walletRepository.save(wallet);
        log.info("Amount {} credited to wallet {}", amount, walletId);
    }   

    public BigDecimal getBalance(Long walletId){
        log.info("Getting balance for wallet {}", walletId);
        return getWalletById(walletId).getBalance();
    }
}
