package com.my.commandservice.dto.request;

import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.enumeration.LoanStatus;
import com.my.commandservice.entity.enumeration.LoanType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestructureLoanRequest {


    private LoanType loanType;

    private BigDecimal principalAmount;

    private BigDecimal remainingBalance;

    private BigDecimal interestRate;

    private Integer termMonths;

    private BigDecimal monthlyPayment;

    private LocalDateTime endDate;

    private LocalDateTime nextPaymentDate;

    private Integer missedPayments;

    private String purpose;


}
