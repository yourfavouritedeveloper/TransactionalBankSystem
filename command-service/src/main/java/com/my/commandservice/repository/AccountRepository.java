package com.my.commandservice.repository;

import com.my.commandservice.entity.Account;
import com.my.commandservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByIban(String iban);

    boolean existsByIban(String iban);

    Optional<Account> findByUser(User user);
}
