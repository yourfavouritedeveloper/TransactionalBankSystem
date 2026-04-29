package com.my.commandservice.dto.request;

import com.my.commandservice.dto.response.AccountResponse;
import com.my.commandservice.entity.enumeration.TransactionDirection;
import com.my.commandservice.entity.enumeration.TransactionStatus;
import com.my.commandservice.entity.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTransactionRequest {

    private UUID fromAccountId;

    private UUID toAccountId;

    private BigDecimal amount;

    private TransactionType type;

    private TransactionStatus status;

    private TransactionDirection direction;
}
