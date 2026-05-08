package com.my.queryservice.repository.jpa;

import com.my.queryservice.entity.Account;
import com.my.queryservice.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanRepository extends JpaRepository<Loan, UUID> {
    Optional<Loan> findByAccount(Account account);

    Optional<Loan> findByLoanNumber(String loanNumber);
}
