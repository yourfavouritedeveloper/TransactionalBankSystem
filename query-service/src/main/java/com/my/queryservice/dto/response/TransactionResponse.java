package com.my.queryservice.dto.response;


import com.my.queryservice.entity.enumeration.TransactionDirection;
import com.my.queryservice.entity.enumeration.TransactionStatus;
import com.my.queryservice.entity.enumeration.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    private LocalDateTime scheduledAt;

    private TransactionDirection direction;

}
