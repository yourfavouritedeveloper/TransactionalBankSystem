package com.my.queryservice.service;

import com.my.queryservice.dto.response.AccountResponse;
import com.my.queryservice.dto.response.TransactionResponse;
import com.my.queryservice.dto.response.UserResponse;
import com.my.queryservice.entity.Account;
import com.my.queryservice.entity.Transaction;
import com.my.queryservice.entity.User;
import com.my.queryservice.entity.enumeration.*;
import com.my.queryservice.exceptions.AccountNotFoundException;
import com.my.queryservice.exceptions.TransactionNotFoundException;
import com.my.queryservice.repository.jpa.AccountRepository;
import com.my.queryservice.repository.jpa.TransactionRepository;
import com.my.queryservice.repository.redis.TransactionRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionRedisRepository transactionRedisRepository;
    private final AccountRepository accountRepository;

    @Transactional(readOnly = true)
    public TransactionResponse findById(UUID id) {
        Transaction transaction = transactionRedisRepository.findById(id).orElseGet(() -> {
            Transaction entity = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
            transactionRedisRepository.save(entity);
            return entity;
        });
        return toTransactionResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findByFromAccount(UUID fromId) {
        List<Transaction> transactions = transactionRedisRepository.findByFromAccount(fromId);

        if (transactions.isEmpty()) {
            Account account = accountRepository.findById(fromId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            transactions = transactionRepository.findByFromAccount(account);
            if (transactions.isEmpty()) throw new TransactionNotFoundException("No transactions found for account");
            transactions.forEach(transactionRedisRepository::save);
        }

        return transactions.stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findByToAccount(UUID toId) {
        List<Transaction> transactions = transactionRedisRepository.findByToAccount(toId);

        if (transactions.isEmpty()) {
            Account account = accountRepository.findById(toId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            transactions = transactionRepository.findByToAccount(account);
            if (transactions.isEmpty()) throw new TransactionNotFoundException("No transactions found for account");
            transactions.forEach(transactionRedisRepository::save);
        }

        return transactions.stream()
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }



    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll(TransactionStatus status, TransactionType type, TransactionDirection direction) {
        List<Transaction> cached = transactionRedisRepository.findAll();

        List<Transaction> transactions = cached.isEmpty() ? transactionRepository.findAll() : cached;

        if (cached.isEmpty()) {
            transactions.forEach(transactionRedisRepository::save);
        }

        return transactions.stream()
                .filter(t -> status == null || t.getStatus() == status)
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> direction == null || t.getDirection() == direction)
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findAllScheduled() {
        List<Transaction> cached = transactionRedisRepository.findAll();

        List<Transaction> transactions = cached.isEmpty() ? transactionRepository.findAll() : cached;

        if (cached.isEmpty()) {
            transactions.forEach(transactionRedisRepository::save);
        }

        return transactions.stream()
                .filter(t -> t.getScheduledAt() != null && t.getScheduledAt().isAfter(LocalDateTime.now()))
                .map(this::toTransactionResponse)
                .collect(Collectors.toList());
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .direction(transaction.getDirection())
                .scheduledAt(transaction.getScheduledAt())
                .fromAccount(toAccountResponse(transaction.getFromAccount()))
                .toAccount(toAccountResponse(transaction.getToAccount()))
                .build();
    }



    private AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .holdBalance(account.getHoldBalance())
                .status(account.getStatus())
                .iban(account.getIban())
                .type(account.getType())
                .currency(account.getCurrency())
                .user(toUserResponse(account.getUser()))
                .build();
    }



    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }
}
