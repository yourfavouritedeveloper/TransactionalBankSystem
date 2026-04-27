package com.my.queryservice.service;

import com.my.queryservice.dto.response.AccountResponse;
import com.my.queryservice.dto.response.UserResponse;
import com.my.queryservice.entity.Account;
import com.my.queryservice.entity.User;
import com.my.queryservice.entity.enumeration.AccountStatus;
import com.my.queryservice.entity.enumeration.AccountType;
import com.my.queryservice.exceptions.AccountNotFoundException;
import com.my.queryservice.exceptions.UserNotFoundException;
import com.my.queryservice.repository.jpa.AccountRepository;
import com.my.queryservice.repository.jpa.UserRepository;
import com.my.queryservice.repository.redis.AccountRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;
    private final AccountRedisRepository accountRedisRepository;

    @Transactional(readOnly = true)
    public AccountResponse findById(UUID id) {
        Account account = accountRedisRepository.findById(id).orElseGet(() -> {
            Account entity = accountRepository.findById(id)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            accountRedisRepository.save(entity);
            return entity;
        });
        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByUser(UUID userId) {
        Account account = accountRedisRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            Account entity = accountRepository.findByUser(user)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            accountRedisRepository.save(entity);
            return entity;
        });
        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByIban(String iban) {
        Account account = accountRedisRepository.findByIban(iban).orElseGet(() -> {
            Account entity = accountRepository.findByIban(iban)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            accountRedisRepository.save(entity);
            return entity;
        });
        return toAccountResponse(account);
    }

    public List<AccountResponse> findAll(AccountStatus status, AccountType type) {
        List<Account> cached = accountRedisRepository.findAll();

        List<Account> accounts = cached.isEmpty() ? accountRepository.findAll() : cached;

        if (cached.isEmpty()) {
            accounts.forEach(accountRedisRepository::save);
        }

        return accounts.stream()
                .filter(a -> status == null || a.getStatus() == status)
                .filter(a -> type == null || a.getType() == type)
                .map(this::toAccountResponse)
                .collect(Collectors.toList());
    }

    private AccountResponse toAccountResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .balance(account.getBalance())
                .status(account.getStatus())
                .iban(account.getIban())
                .type(account.getType())
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
