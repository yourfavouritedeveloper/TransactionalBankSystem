package com.my.commandservice.dto.response;

import com.my.commandservice.entity.enumeration.TransactionStatus;
import com.my.commandservice.entity.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {

    private UUID id;

    private AccountResponse fromAccount;

    private AccountResponse toAccount;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

}
