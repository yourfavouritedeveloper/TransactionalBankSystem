package com.my.commandservice.service;

import com.my.commandservice.dto.request.UpdateTransactionRequest;
import com.my.commandservice.dto.response.AccountResponse;
import com.my.commandservice.dto.response.TransactionResponse;
import com.my.commandservice.dto.response.TransferResponse;
import com.my.commandservice.dto.response.UserResponse;
import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.Transaction;
import com.my.commandservice.entity.User;
import com.my.commandservice.entity.enumeration.*;
import com.my.commandservice.exceptions.*;
import com.my.commandservice.repository.AccountRepository;
import com.my.commandservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CurrencyService currencyService;



    @Transactional
    public TransactionResponse deposit(String iban, UUID id, BigDecimal amount, Currency currency) {

        if (id == null && iban == null) {
            throw new InvalidValidationException("Either id or iban must be provided");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account toAccount = (id != null) ? accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found")) :
        accountRepository.findByIban(iban)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (toAccount.getStatus() == AccountStatus.PENDING ||
                toAccount.getStatus() == AccountStatus.BLOCKED ||
                toAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }


        Transaction transaction = Transaction.builder()
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.DEPOSIT)
                .direction(TransactionDirection.IN)
                .status(TransactionStatus.PENDING)
                .currency(currency)
                .build();

        transactionRepository.save(transaction);

        BigDecimal amountOf = (!currency.equals(toAccount.getCurrency())) ?
                currencyService.convert(currency,toAccount.getCurrency(),amount)
                : amount;


        toAccount.setBalance(toAccount.getBalance().add(amountOf));
        accountRepository.save(toAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return toTransactionResponse(transaction);
    }



    @Transactional
    public TransactionResponse withdraw(UUID id, BigDecimal amount, Currency currency) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (fromAccount.getStatus() == AccountStatus.PENDING ||
                fromAccount.getStatus() == AccountStatus.BLOCKED ||
                fromAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        BigDecimal amountOf = (!currency.equals(fromAccount.getCurrency())) ?
                currencyService.convert(currency,fromAccount.getCurrency(),amount)
                : amount;

        if (fromAccount.getBalance().compareTo(amountOf) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account for that operation");
        }

        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .amount(amount)
                .type(TransactionType.WITHDRAWAL)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(currency)
                .build();

        transactionRepository.save(transaction);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amountOf));
        accountRepository.save(fromAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return toTransactionResponse(transaction);


    }




    @Transactional
    public TransactionResponse transfer(UUID fromAccountId, UUID toAccountId, String toAccountIban , BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        if (toAccountId == null && toAccountIban == null) {
            throw new InvalidValidationException("Either id or iban must be provided");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Account toAccount = (toAccountId != null) ? accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found")) :
                accountRepository.findByIban(toAccountIban)
                        .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (toAccount.getStatus() == AccountStatus.PENDING ||
                toAccount.getStatus() == AccountStatus.BLOCKED ||
                toAccount.getStatus() == AccountStatus.CLOSED ||
                fromAccount.getStatus() == AccountStatus.PENDING ||
                fromAccount.getStatus() == AccountStatus.BLOCKED ||
                fromAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        BigDecimal amountOf = (!fromAccount.getCurrency().equals(toAccount.getCurrency())) ?
                currencyService.convert(toAccount.getCurrency(),fromAccount.getCurrency(),amount)
                : amount;

        if (fromAccount.getBalance().compareTo(amountOf) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account for that operation");
        }

        Transaction fromTransaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amountOf)
                .type(TransactionType.TRANSFER)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(fromAccount.getCurrency())
                .build();


        transactionRepository.save(fromTransaction);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amountOf));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        fromTransaction.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(fromTransaction);

        return toTransactionResponse(fromTransaction);

    }






    @Transactional
    public TransactionResponse refund(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        Account fromAccount = transaction.getToAccount();
        Account toAccount = transaction.getFromAccount();

        if (toAccount.getStatus() == AccountStatus.PENDING ||
                toAccount.getStatus() == AccountStatus.BLOCKED ||
                toAccount.getStatus() == AccountStatus.CLOSED ||
                fromAccount.getStatus() == AccountStatus.PENDING ||
                fromAccount.getStatus() == AccountStatus.BLOCKED ||
                fromAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        BigDecimal amountOf = (!fromAccount.getCurrency().equals(toAccount.getCurrency())) ?
                currencyService.convert(toAccount.getCurrency(),fromAccount.getCurrency(),transaction.getAmount())
                : transaction.getAmount();

        if (fromAccount.getBalance().compareTo(transaction.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account for that operation");
        }

        Transaction fromRefund = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(transaction.getAmount())
                .type(TransactionType.REFUND)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(fromAccount.getCurrency())
                .build();


        transactionRepository.save(fromRefund);

        fromAccount.setBalance(fromAccount.getBalance().subtract(transaction.getAmount()));
        toAccount.setBalance(toAccount.getBalance().add(amountOf));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        fromRefund.setStatus(TransactionStatus.REFUNDED);

        transactionRepository.save(fromRefund);

        return toTransactionResponse(fromRefund);


    }


    @Transactional
    public TransactionResponse reversal(UUID originalTransactionId, String reason) {
        Transaction original = transactionRepository.findById(originalTransactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (original.getStatus() != TransactionStatus.SUCCESS) {
            throw new InvalidTransactionStatusException("Only successful transactions can be reversed");
        }

        Account debitAccount  = original.getToAccount();
        Account creditAccount = original.getFromAccount();

        if (debitAccount == null || creditAccount == null) {
            throw new InvalidValidationException("Cannot reverse a transaction without both accounts");
        }

        if (debitAccount.getBalance().compareTo(original.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient balance to process reversal");
        }

        Transaction reversalTransaction = Transaction.builder()
                .fromAccount(debitAccount)
                .toAccount(creditAccount)
                .amount(original.getAmount())
                .type(TransactionType.REVERSAL)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(original.getCurrency())
                .build();

        transactionRepository.save(reversalTransaction);

        debitAccount.setBalance(debitAccount.getBalance().subtract(original.getAmount()));
        creditAccount.setBalance(creditAccount.getBalance().add(original.getAmount()));

        accountRepository.save(debitAccount);
        accountRepository.save(creditAccount);

        original.setStatus(TransactionStatus.REVERSED);
        transactionRepository.save(original);

        reversalTransaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(reversalTransaction);

        return toTransactionResponse(reversalTransaction);
    }


    @Transactional
    public TransactionResponse fee(UUID fromAccountId, BigDecimal amount, UUID toAccountId) {
        if(amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (toAccount.getStatus() == AccountStatus.PENDING ||
                toAccount.getStatus() == AccountStatus.BLOCKED ||
                toAccount.getStatus() == AccountStatus.CLOSED ||
                fromAccount.getStatus() == AccountStatus.PENDING ||
                fromAccount.getStatus() == AccountStatus.BLOCKED ||
                fromAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        if (toAccount.getType()!=AccountType.SYSTEM) {
            throw new InvalidAccountTypeException("Account type must be system");
        }

        BigDecimal amountOf = (!fromAccount.getCurrency().equals(toAccount.getCurrency())) ?
                currencyService.convert(toAccount.getCurrency(),fromAccount.getCurrency(),amount)
                : amount;

        if (fromAccount.getBalance().compareTo(amountOf) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account for that operation");
        }

        Transaction fromTransaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amountOf)
                .type(TransactionType.FEE)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(fromAccount.getCurrency())
                .build();


        transactionRepository.save(fromTransaction);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amountOf));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        fromTransaction.setStatus(TransactionStatus.SUCCESS);

        transactionRepository.save(fromTransaction);

        return toTransactionResponse(fromTransaction);

    }



    @Transactional
    public TransactionResponse hold(UUID accountId, BigDecimal amount, Currency currency) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        Account fromAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        if (fromAccount.getStatus() == AccountStatus.PENDING ||
                fromAccount.getStatus() == AccountStatus.BLOCKED ||
                fromAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        BigDecimal amountOf = (!currency.equals(fromAccount.getCurrency())) ?
                currencyService.convert(currency,fromAccount.getCurrency(),amount)
                : amount;

        if (fromAccount.getBalance().compareTo(amountOf) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account for that operation");
        }

        Transaction transaction = Transaction.builder()
                .fromAccount(fromAccount)
                .amount(amountOf)
                .type(TransactionType.HOLD)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(fromAccount.getCurrency())
                .build();

        transactionRepository.save(transaction);

        fromAccount.setBalance(fromAccount.getBalance().subtract(amountOf));
        fromAccount.setHoldBalance(fromAccount.getHoldBalance().add(amountOf));
        accountRepository.save(fromAccount);

        transaction.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(transaction);

        return toTransactionResponse(transaction);
    }



    @Transactional
    public TransactionResponse release(UUID id) {

        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        Account toAccount = transaction.getFromAccount();

        if (toAccount.getStatus() == AccountStatus.PENDING ||
                toAccount.getStatus() == AccountStatus.BLOCKED ||
                toAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        Transaction release = Transaction.builder()
                .toAccount(toAccount)
                .amount(transaction.getAmount())
                .type(TransactionType.RELEASE)
                .direction(TransactionDirection.IN)
                .status(TransactionStatus.PENDING)
                .currency(toAccount.getCurrency())
                .build();

        transactionRepository.save(release);

        toAccount.setBalance(toAccount.getBalance().add(toAccount.getHoldBalance()));
        toAccount.setHoldBalance(BigDecimal.ZERO);
        accountRepository.save(toAccount);

        release.setStatus(TransactionStatus.SUCCESS);
        transactionRepository.save(release);

        return toTransactionResponse(release);
    }



    @Transactional
    public TransactionResponse schedule(UUID fromAccountId, UUID toAccountId,
                                        BigDecimal amount,
                                        LocalDateTime scheduledAt) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        if (scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now())) {
            throw new InvalidValidationException("Scheduled date must be in the future");
        }

        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));

        Account toAccount =  accountRepository.findById(toAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));


        if (toAccount.getStatus() == AccountStatus.PENDING ||
                toAccount.getStatus() == AccountStatus.BLOCKED ||
                toAccount.getStatus() == AccountStatus.CLOSED ||
                fromAccount.getStatus() == AccountStatus.PENDING ||
                fromAccount.getStatus() == AccountStatus.BLOCKED ||
                fromAccount.getStatus() == AccountStatus.CLOSED) {
            throw new InvalidAccountStatusException("Account is not available for given operation");
        }

        BigDecimal amountOf = (!fromAccount.getCurrency().equals(toAccount.getCurrency())) ?
                currencyService.convert(toAccount.getCurrency(),fromAccount.getCurrency(),amount)
                : amount;

        if (fromAccount.getBalance().compareTo(amountOf) < 0) {
            throw new InsufficientBalanceException("Insufficient balance in account for that operation");
        }

        Transaction scheduledTransaction = Transaction.builder()
                .fromAccount(fromAccount)
                .toAccount(toAccount)
                .amount(amount)
                .type(TransactionType.SCHEDULED)
                .direction(TransactionDirection.OUT)
                .status(TransactionStatus.PENDING)
                .currency(toAccount.getCurrency())
                .scheduledAt(scheduledAt)
                .build();

        transactionRepository.save(scheduledTransaction);

        return toTransactionResponse(scheduledTransaction);
    }


    @Transactional
    public TransactionResponse changeScheduleTime(UUID transactionId, LocalDateTime scheduledAt) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (scheduledAt == null || scheduledAt.isBefore(transaction.getScheduledAt())) {
            throw new InvalidValidationException("Scheduled date must be in the future");
        }

        if(transaction.getType() != TransactionType.SCHEDULED) {
            throw new InvalidValidationException("Transaction must be scheduled");
        }

        transaction.setScheduledAt(scheduledAt);
        transactionRepository.save(transaction);
        return toTransactionResponse(transaction);

    }


    @Transactional
    public TransactionResponse cancelSchedule(UUID transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        transaction.setStatus(TransactionStatus.CANCELLED);
        transactionRepository.save(transaction);
        return toTransactionResponse(transaction);
    }



    @Scheduled(fixedRate = 60000)
    @Transactional
    public void processScheduledTransactions() {
        List<Transaction> due = transactionRepository
                .findByTypeAndStatusAndScheduledAtBefore(
                        TransactionType.SCHEDULED,
                        TransactionStatus.PENDING,
                        LocalDateTime.now()
                );

        for (Transaction t : due) {
            try {
                transfer(t.getFromAccount().getId(), t.getToAccount().getId(), null, t.getAmount());
                t.setStatus(TransactionStatus.SUCCESS);
            } catch (Exception e) {
                t.setStatus(TransactionStatus.FAILED);
            }
            transactionRepository.save(t);
        }
    }


    @Transactional
    public TransactionResponse update(UUID id, UpdateTransactionRequest updateTransactionRequest) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        if (updateTransactionRequest.getFromAccountId() != null) {
            Account fromAccount = accountRepository.findById(updateTransactionRequest.getFromAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            transaction.setFromAccount(fromAccount);
        }
        if (updateTransactionRequest.getToAccountId() != null) {
            Account toAccount = accountRepository.findById(updateTransactionRequest.getToAccountId())
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));
            transaction.setToAccount(toAccount);
        }
        if (updateTransactionRequest.getAmount() != null) transaction.setAmount(updateTransactionRequest.getAmount());
        if (updateTransactionRequest.getType() != null) transaction.setType(updateTransactionRequest.getType());
        if (updateTransactionRequest.getDirection() != null) transaction.setDirection(updateTransactionRequest.getDirection());
        if (updateTransactionRequest.getStatus() != null) transaction.setStatus(updateTransactionRequest.getStatus());

        transactionRepository.save(transaction);

        return toTransactionResponse(transaction);

    }


    @Transactional
    public void delete(UUID id) {
        transactionRepository.deleteById(id);
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
