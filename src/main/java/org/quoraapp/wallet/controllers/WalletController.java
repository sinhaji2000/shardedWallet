package org.quoraapp.wallet.controllers;

import java.math.BigDecimal;

import org.quoraapp.wallet.dtos.CreateWalletRequestDTO;
import org.quoraapp.wallet.dtos.CreditwalletRequestDTO;
import org.quoraapp.wallet.dtos.DebitWalletRequestDTO;
import org.quoraapp.wallet.entities.Wallet;
import org.quoraapp.wallet.service.WalletService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/wallets")
public class WalletController {
    
    private final WalletService walletService;

    @PostMapping
    public ResponseEntity<Wallet> createWallet(@RequestBody CreateWalletRequestDTO reques) {
        
        try{
            Wallet newWallet = walletService.createWallet(reques.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body(newWallet);
        }catch(Exception e){
            log.error("Error creating wallet: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Wallet> getWalletById(@PathVariable Long id) {
        try{
            Wallet wallet = walletService.getWalletById(id);
            if(wallet != null){
                return ResponseEntity.ok(wallet);
            }else{
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
        }catch(Exception e){
            log.error("Error fetching wallet: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getWalletBalance(@PathVariable Long id) {
        try{
            BigDecimal balance = walletService.getBalance(id);
            return ResponseEntity.ok(balance);
        }catch(Exception e){
            log.error("Error fetching wallet balance: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/debit")
    public ResponseEntity<Wallet> debitWallet(@PathVariable Long id, @RequestBody DebitWalletRequestDTO request) {
        //TODO: process POST request
        
        try{
            walletService.debit(id, request.getAmount());
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(wallet);
        }catch(Exception e){
            log.error("Error debiting wallet: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/{id}/credit")
    public ResponseEntity<Wallet> creditWallet(@PathVariable Long id, @RequestBody CreditwalletRequestDTO request) {
        //TODO: process POST request

        try{
            walletService.credit(id, request.getAmount());
            Wallet wallet = walletService.getWalletById(id);
            return ResponseEntity.ok(wallet);
        }catch(Exception e){
            log.error("Error crediting wallet: {}", e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }
    
    

    
}
