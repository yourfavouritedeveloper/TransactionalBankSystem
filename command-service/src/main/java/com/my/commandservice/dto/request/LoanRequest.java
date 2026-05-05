package com.my.commandservice.dto.request;

import com.my.commandservice.entity.enumeration.LoanStatus;
import com.my.commandservice.entity.enumeration.LoanType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanRequest {

    @NotNull(message = "Account Id is required")
    private UUID accountId;

    @NotNull(message = "Loan Type is required")
    private LoanType loanType;

    @NotNull(message = "Principal amount is required")
    private BigDecimal principalAmount;

    @NotNull(message = "Term month is required")
    private Integer termMonths;

    private String purpose;

}
