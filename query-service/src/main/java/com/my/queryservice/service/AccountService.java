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

    @Transactional(readOnly = true)
    public AccountResponse findById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Account account = accountRepository.findByUser(user)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public AccountResponse findByIban(String iban) {
        Account account = accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        return toAccountResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountResponse> findAll(AccountStatus status, AccountType type) {
        List<Account> accounts = accountRepository.findAll();

        if (status != null && type != null) {
            return accounts.stream()
                    .filter(account -> account.getStatus() == status && account.getType() == type)
                    .map(this::toAccountResponse)
                    .collect(Collectors.toList());
        }

        if (status != null) {
            return accounts.stream()
                    .filter(account -> account.getStatus() == status)
                    .map(this::toAccountResponse)
                    .collect(Collectors.toList());
        }

        if (type != null) {
            return accounts.stream()
                    .filter(account -> account.getType() == type)
                    .map(this::toAccountResponse)
                    .collect(Collectors.toList());
        }

        return accounts.stream()
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
