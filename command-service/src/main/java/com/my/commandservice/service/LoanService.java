package com.my.commandservice.service;

import com.my.commandservice.dto.request.LoanRequest;
import com.my.commandservice.dto.request.RestructureLoanRequest;
import com.my.commandservice.dto.request.UpdateLoanRequest;
import com.my.commandservice.dto.response.LoanResponse;
import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.Loan;
import com.my.commandservice.entity.enumeration.AccountStatus;
import com.my.commandservice.entity.enumeration.LoanStatus;
import com.my.commandservice.entity.enumeration.LoanType;
import com.my.commandservice.exceptions.*;
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


    public LoanResponse updateStatus(UUID id, LoanStatus status) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        loan.setStatus(status);
        loanRepository.save(loan);
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


    @Transactional
    public LoanResponse requestRestructure(UUID id, String reason) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        loan.setRequestChange(Boolean.TRUE);
        loan.setRequestReason(reason);
        loan.setStatus(LoanStatus.PENDING);
        loanRepository.save(loan);

        return toLoanResponse(loan);

    }


    @Transactional
    public LoanResponse applyRestructureRequest(UUID id, RestructureLoanRequest loanRequest) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        loan.setRequestChange(Boolean.FALSE);
        loan.setRequestReason("");
        loan.setStatus(LoanStatus.ACTIVE);
        if(loanRequest.getLoanType() != null) loan.setLoanType(loanRequest.getLoanType());
        if(loanRequest.getEndDate() != null) loan.setEndDate(loanRequest.getEndDate());
        if(loanRequest.getTermMonths() != null) loan.setTermMonths(loanRequest.getTermMonths());
        if(loanRequest.getNextPaymentDate() != null) loan.setNextPaymentDate(loanRequest.getNextPaymentDate());
        if(loanRequest.getPurpose() != null) loan.setPurpose(loanRequest.getPurpose());
        if(loanRequest.getInterestRate() != null) loan.setInterestRate(loanRequest.getInterestRate());
        if(loanRequest.getPrincipalAmount() != null) loan.setRemainingBalance(loanRequest.getPrincipalAmount());
        if(loanRequest.getMissedPayments() != null) loan.setMissedPayments(loanRequest.getMissedPayments());
        if(loanRequest.getMonthlyPayment() != null) loan.setMonthlyPayment(loanRequest.getMonthlyPayment());

        loanRepository.save(loan);
        return toLoanResponse(loan);
    }

    @Transactional
    public LoanResponse update(UUID id, UpdateLoanRequest loanRequest) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        if(loanRequest.getRequestChange() != null) loan.setRequestChange(loanRequest.getRequestChange());
        if(loanRequest.getRequestReason() != null) loan.setRequestReason(loanRequest.getRequestReason());
        if(loanRequest.getStartDate() != null) loan.setStartDate(loanRequest.getStartDate());
        if(loanRequest.getStatus() != null) loan.setStatus(loanRequest.getStatus());
        if(loanRequest.getTotalAmountPaid() != null) loan.setTotalAmountPaid(loanRequest.getTotalAmountPaid());
        if(loanRequest.getTotalInterestPaid() != null) loan.setTotalInterestPaid(loanRequest.getTotalInterestPaid());
        if(loanRequest.getLoanType() != null) loan.setLoanType(loanRequest.getLoanType());
        if(loanRequest.getRemainingBalance() != null) loan.setRemainingBalance(loanRequest.getRemainingBalance());
        if(loanRequest.getEndDate() != null) loan.setEndDate(loanRequest.getEndDate());
        if(loanRequest.getTermMonths() != null) loan.setTermMonths(loanRequest.getTermMonths());
        if(loanRequest.getNextPaymentDate() != null) loan.setNextPaymentDate(loanRequest.getNextPaymentDate());
        if(loanRequest.getPurpose() != null) loan.setPurpose(loanRequest.getPurpose());
        if(loanRequest.getInterestRate() != null) loan.setInterestRate(loanRequest.getInterestRate());
        if(loanRequest.getPrincipalAmount() != null) loan.setRemainingBalance(loanRequest.getPrincipalAmount());
        if(loanRequest.getMissedPayments() != null) loan.setMissedPayments(loanRequest.getMissedPayments());
        if(loanRequest.getMonthlyPayment() != null) loan.setMonthlyPayment(loanRequest.getMonthlyPayment());

        loanRepository.save(loan);

        return toLoanResponse(loan);
    }

    @Transactional
    public LoanResponse close(UUID id) {
        Loan loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException("Loan not found"));

        Account account = loan.getAccount();

        if(account.getStatus() == AccountStatus.BLOCKED ||
                account.getStatus() == AccountStatus.PENDING ||
                account.getStatus() == AccountStatus.CLOSED ||
                account.getStatus() == AccountStatus.FROZEN) {
            throw new InvalidAccountStatusException("Account status is not eligible for this action");
        }

        if(loan.getRemainingBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new LoanNotFinishedException("Remaining balance is not zero");
        }

        loan.setStatus(LoanStatus.CLOSED);
        loanRepository.save(loan);
        return toLoanResponse(loan);
    }


    @Transactional
    public void delete(UUID id) {
        loanRepository.deleteById(id);
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
