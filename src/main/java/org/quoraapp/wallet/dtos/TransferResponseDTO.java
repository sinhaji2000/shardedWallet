package org.quoraapp.wallet.dtos;


import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDTO {
    private Long sagaInstanceId ;
}
