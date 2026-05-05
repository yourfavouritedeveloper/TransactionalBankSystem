package com.my.commandservice.service;

import com.my.commandservice.dto.request.LoanRequest;
import com.my.commandservice.dto.response.LoanResponse;
import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.Loan;
import com.my.commandservice.entity.enumeration.AccountStatus;
import com.my.commandservice.entity.enumeration.LoanStatus;
import com.my.commandservice.exceptions.AccountNotFoundException;
import com.my.commandservice.exceptions.InsufficientBalanceException;
import com.my.commandservice.exceptions.InvalidAccountStatusException;
import com.my.commandservice.exceptions.LoanNotFoundException;
import com.my.commandservice.repository.AccountRepository;
import com.my.commandservice.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public LoanResponse createLoan(LoanRequest loanRequest) {
        Account account = accountRepository.findById(loanRequest.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(account.getStatus() == AccountStatus.BLOCKED ||
        account.getStatus() == AccountStatus.PENDING ||
        account.getStatus() == AccountStatus.CLOSED ||
        account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        Loan loan = Loan.builder()
                .loanNumber(generateLoanNumber())
                .loanType(loanRequest.getLoanType())
                .account(account)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(loanRequest.getTermMonths()))
                .termMonths(loanRequest.getTermMonths())
                .nextPaymentDate(LocalDateTime.now().plusMonths(1))
                .principalAmount(loanRequest.getPrincipalAmount())
                .purpose(loanRequest.getPurpose())
                .status(LoanStatus.PENDING)
                .interestRate(BigDecimal.ZERO)
                .remainingBalance(loanRequest.getPrincipalAmount())
                .totalInterestPaid(BigDecimal.ZERO)
                .missedPayments(0)
                .monthlyPayment(loanRequest.getPrincipalAmount().divide(BigDecimal.valueOf(loanRequest.getTermMonths())))
                .totalAmountPaid(BigDecimal.ZERO)
                .build();

        loanRepository.save(loan);

        account.setBalance(account.getBalance().add(loanRequest.getPrincipalAmount()));

        return toLoanResponse(loan);
    }


    @Transactional
    public LoanResponse makePayment(UUID id, BigDecimal amount) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        if(account.getBalance().compareTo(loan.getMonthlyPayment()) < 0) {
            throw new InsufficientBalanceException("No enough balance to pay loan");
        }

        BigDecimal interestRate = ((loan.getMonthlyPayment()
                .divide(amount)).subtract(BigDecimal.valueOf(100)))
                .divide(BigDecimal.valueOf(100));

        BigDecimal interest = loan.getMonthlyPayment().multiply(interestRate);

        BigDecimal totalInterest = loan.getTotalInterestPaid().add(interest);

        BigDecimal remainingBalance = (loan.getMonthlyPayment().compareTo(amount) < 0) ?
                loan.getMonthlyPayment().subtract(amount) : BigDecimal.ZERO;



        loan.setInterestRate(interestRate);
        loan.setNextPaymentDate(LocalDateTime.now().plusMonths(1));
        loan.setRemainingBalance(remainingBalance);
        loan.setTotalAmountPaid(loan.getTotalAmountPaid().add(amount));
        loan.setTotalInterestPaid(totalInterest);

        account.setBalance(account.getBalance().subtract(amount));

        loanRepository.save(loan);
        accountRepository.save(account);

        return toLoanResponse(loan);
    }


    @Transactional
    public LoanResponse earlyPayment(UUID id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        if(account.getBalance().compareTo(loan.getRemainingBalance()) < 0) {
            throw new InsufficientBalanceException("No enough balance to pay loan");
        }


        loan.setRemainingBalance(BigDecimal.ZERO);
        loan.setTotalAmountPaid(loan.getTotalAmountPaid().add(loan.getRemainingBalance()));

        account.setBalance(account.getBalance().subtract(loan.getRemainingBalance()));

        loanRepository.save(loan);
        accountRepository.save(account);

        return toLoanResponse(loan);
    }

    private String generateLoanNumber() {
        String prefix = "LN";
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = String.format("%04d", new Random().nextInt(9999));
        return prefix + "-" + timestamp + "-" + random;
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
