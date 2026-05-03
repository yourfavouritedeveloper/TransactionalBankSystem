package com.my.queryservice.dto.request;


import jakarta.validation.constraints.NotNull;
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
public class TransactionRequest {

    @NotNull(message = "From Account is required")
    private UUID fromAccount;

    private UUID toAccount;

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

}
