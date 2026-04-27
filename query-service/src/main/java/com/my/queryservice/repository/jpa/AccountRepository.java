package com.my.queryservice.repository.jpa;

import com.my.queryservice.entity.Account;
import com.my.queryservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Optional<Account> findByUser(User user);

    Optional<Account> findByIban(String iban);
}
