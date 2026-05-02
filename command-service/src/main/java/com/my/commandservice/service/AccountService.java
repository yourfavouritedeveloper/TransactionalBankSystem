package com.my.commandservice.service;

import com.my.commandservice.dto.request.AccountRequest;
import com.my.commandservice.dto.request.UpdateAccountRequest;
import com.my.commandservice.dto.response.AccountResponse;
import com.my.commandservice.dto.response.UserResponse;
import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.User;
import com.my.commandservice.entity.enumeration.AccountStatus;
import com.my.commandservice.exceptions.AccountNotFoundException;
import com.my.commandservice.exceptions.InvalidAccountStatusException;
import com.my.commandservice.exceptions.UserNotFoundException;
import com.my.commandservice.repository.AccountRepository;
import com.my.commandservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    private static final String COUNTRY_CODE = "AZ";
    private static final String BANK_CODE = "BANK";

    @Transactional
    public AccountResponse createAccount(AccountRequest accountRequest) {
        User user = userRepository.findById(accountRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Account account = Account.builder()
                .type(accountRequest.getType())
                .iban(generateIban())
                .currency(accountRequest.getCurrency())
                .user(user)
                .build();

        accountRepository.save(account);

        return toAccountResponse(account);
    }

    @Transactional
    public AccountResponse updateStatus(UUID id, AccountStatus status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (account.getStatus() == status) {
            throw new InvalidAccountStatusException("Account already has this status");
        }

        if(account.getStatus() == AccountStatus.CLOSED || account.getStatus() == AccountStatus.BLOCKED) {
            throw new InvalidAccountStatusException("Cannot change the status of closed or blocked accounts");
        }

        account.setStatus(status);
        accountRepository.save(account);

        return toAccountResponse(account);
    }

    @Transactional
    public AccountResponse update(UUID id, UpdateAccountRequest accountRequest) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if(accountRequest.getType() != null) account.setType(accountRequest.getType());
        if(accountRequest.getCurrency() != null) account.setCurrency(accountRequest.getCurrency());
        if(accountRequest.getBalance() != null) account.setBalance(accountRequest.getBalance());
        if(accountRequest.getStatus() != null) account.setStatus(accountRequest.getStatus());

        accountRepository.save(account);
        return toAccountResponse(account);
    }

    @Transactional
    public void closeAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    @Transactional
    public void deleteAccount(UUID id) {
        accountRepository.deleteById(id);
    }

    private String generateIban() {

        while (true) {
            long timestamp = System.currentTimeMillis();
            long randomPart = ThreadLocalRandom.current().nextLong(100000, 999999);

            String iban = COUNTRY_CODE +
                    "00" +
                    BANK_CODE +
                    timestamp +
                    randomPart;

            boolean exists = accountRepository.existsByIban(iban);

            if (!exists) {
                return iban;
            }
        }
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
