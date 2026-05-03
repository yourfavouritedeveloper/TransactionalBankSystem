package com.my.queryservice.repository.jpa;


import com.my.queryservice.entity.Account;
import com.my.queryservice.entity.Transaction;
import com.my.queryservice.entity.enumeration.TransactionStatus;
import com.my.queryservice.entity.enumeration.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByTypeAndStatusAndScheduledAtBefore(TransactionType type, TransactionStatus status, LocalDateTime scheduledAtBefore);

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByFromAccount(Account fromAccount);

    List<Transaction> findByToAccount(Account toAccount);
}
