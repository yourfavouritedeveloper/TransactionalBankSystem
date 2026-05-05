package com.my.commandservice.dto.response;

import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.enumeration.LoanStatus;
import com.my.commandservice.entity.enumeration.LoanType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {

    private UUID id;

    private String loanNumber;

    private UUID account;

    private LoanType loanType;

    private LoanStatus status;

    private BigDecimal principalAmount;

    private BigDecimal remainingBalance;

    private BigDecimal interestRate;

    private Integer termMonths;

    private BigDecimal monthlyPayment;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime nextPaymentDate;

    private BigDecimal totalInterestPaid;

    private BigDecimal totalAmountPaid;

    private Integer missedPayments;

    private String purpose;

    private LocalDateTime createdAt;

}
