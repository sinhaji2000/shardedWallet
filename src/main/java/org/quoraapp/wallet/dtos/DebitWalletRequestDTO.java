package org.quoraapp.wallet.dtos;


import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DebitWalletRequestDTO {
    
    private BigDecimal amount;
}
