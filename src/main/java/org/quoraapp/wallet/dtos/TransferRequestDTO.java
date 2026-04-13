package org.quoraapp.wallet.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
@AllArgsConstructor
@RequiredArgsConstructor
@Data
public class TransferRequestDTO {
    
    private Long fromWalletId ;
    private Long toWalletId ;
    private BigDecimal amount ;
    private String description ;
}
