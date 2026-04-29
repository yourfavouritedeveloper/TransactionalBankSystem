package com.my.commandservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferResponse {

    private TransactionResponse fromTransaction;

    private TransactionResponse toTransaction;

    private String status;
}
