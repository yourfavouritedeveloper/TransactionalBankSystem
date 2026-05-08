package com.my.queryservice.service;

import com.my.queryservice.dto.response.LoanResponse;
import com.my.queryservice.entity.Account;
import com.my.queryservice.entity.Loan;
import com.my.queryservice.entity.enumeration.LoanStatus;
import com.my.queryservice.entity.enumeration.LoanType;
import com.my.queryservice.exceptions.AccountNotFoundException;
import com.my.queryservice.exceptions.UserNotFoundException;
import com.my.queryservice.repository.jpa.AccountRepository;
import com.my.queryservice.repository.jpa.LoanRepository;
import com.my.queryservice.repository.redis.LoanRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;
    private final LoanRedisRepository loanRedisRepository;

    @Transactional(readOnly = true)
    public LoanResponse findById(UUID id) {
        Loan loan = loanRedisRepository.findById(id).orElseGet(() -> {
            Loan entity = loanRepository.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            loanRedisRepository.save(entity);
            return entity;
        });
        return toLoanResponse(loan);
    }

    @Transactional(readOnly = true)
    public LoanResponse findByAccount(UUID accountId) {
        Loan loan = loanRedisRepository.findByAccountId(accountId).orElseGet(() -> {
            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            Loan entity = loanRepository.findByAccount(account)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            loanRedisRepository.save(entity);
            return entity;
        });
        return toLoanResponse(loan);
    }

    @Transactional(readOnly = true)
    public LoanResponse findByLoanNumber(String loanNumber) {
        Loan loan = loanRedisRepository.findByLoanNumber(loanNumber).orElseGet(() -> {
            Loan entity = loanRepository.findByLoanNumber(loanNumber)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            loanRedisRepository.save(entity);
            return entity;
        });
        return toLoanResponse(loan);
    }

    @Transactional(readOnly = true)
    public List<LoanResponse> findAll(LoanStatus status, LoanType type) {
        List<Loan> cached = loanRedisRepository.findAll();

        List<Loan> loans = cached.isEmpty() ? loanRepository.findAll() : cached;

        if (cached.isEmpty()) {
            loans.forEach(loanRedisRepository::save);
        }

        return loans.stream()
                .filter(l -> status == null || l.getStatus() == status)
                .filter(l -> type == null || l.getLoanType() == type)
                .map(this::toLoanResponse)
                .collect(Collectors.toList());
    }

    private LoanResponse toLoanResponse(Loan loan) {
        return LoanResponse.builder()
                .id(loan.getId())
                .loanNumber(loan.getLoanNumber())
                .account(loan.getAccount().getId())
                .loanType(loan.getLoanType())
                .createdAt(loan.getCreatedAt())
                .endDate(loan.getEndDate())
                .interestRate(loan.getInterestRate())
                .missedPayments(loan.getMissedPayments())
                .monthlyPayment(loan.getMonthlyPayment())
                .nextPaymentDate(loan.getNextPaymentDate())
                .principalAmount(loan.getPrincipalAmount())
                .purpose(loan.getPurpose())
                .remainingBalance(loan.getRemainingBalance())
                .startDate(loan.getStartDate())
                .status(loan.getStatus())
                .termMonths(loan.getTermMonths())
                .totalAmountPaid(loan.getTotalAmountPaid())
                .totalInterestPaid(loan.getTotalInterestPaid())
                .build();
    }
}
