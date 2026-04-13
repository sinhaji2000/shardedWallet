package org.quoraapp.wallet.controllers;

import org.quoraapp.wallet.dtos.TransferRequestDTO;
import org.quoraapp.wallet.dtos.TransferResponseDTO;
import org.quoraapp.wallet.entities.Transaction;
import org.quoraapp.wallet.service.TransactionalService;
import org.quoraapp.wallet.service.TransferSagaService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/transactions")
public class TransactionController {
    
    
    private final TransferSagaService transferSagaService;

    @PostMapping()
    public ResponseEntity<TransferResponseDTO>createTransaction(@RequestBody TransferRequestDTO transferRequestDTO){
        
        try{
            log.info("Received transfer request: {}", transferRequestDTO);
            Long sagaInstanceId = transferSagaService.initiateTransfer(
            transferRequestDTO.getFromWalletId() ,
            transferRequestDTO.getToWalletId() ,
            transferRequestDTO.getAmount() ,
            transferRequestDTO.getDescription()  ) ;

            return ResponseEntity.status(HttpStatus.CREATED).body(
                TransferResponseDTO.builder()
                    .sagaInstanceId(sagaInstanceId)
                    .build()

            ) ;

        }catch(Exception e){
            log.error("Error processing transfer request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
