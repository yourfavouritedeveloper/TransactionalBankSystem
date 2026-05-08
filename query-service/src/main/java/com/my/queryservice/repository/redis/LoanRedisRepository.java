package com.my.queryservice.repository.redis;

import com.my.queryservice.entity.Loan;
import com.my.queryservice.repository.jpa.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LoanRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String LOAN_HASH_KEY = "query:loan";
    private static final String LOAN_NUMBER_KEY_PREFIX = "query:loan:loanNumber:";
    private static final String LOAN_ACCOUNT_KEY_PREFIX = "query:loan:account:";
    private final LoanRepository loanRepository;

    public Loan save(Loan loan) {
        template.opsForHash().put(LOAN_HASH_KEY, loan.getId().toString(), loan);
        template.opsForValue().set(LOAN_NUMBER_KEY_PREFIX + loan.getLoanNumber(), loan.getId().toString());
        template.opsForValue().set(LOAN_ACCOUNT_KEY_PREFIX + loan.getAccount().getId().toString(), loan.getId().toString());
        return loan;
    }

    public List<Loan> findAll() {
        return template.opsForHash()
                .values(LOAN_HASH_KEY)
                .stream()
                .map(obj -> obj instanceof Loan l ? l : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Loan> findById(UUID id) {
        Object loan = template.opsForHash().get(LOAN_HASH_KEY, id.toString());
        return Optional.ofNullable(loan instanceof Loan l ? l : null);
    }

    public Optional<Loan> findByLoanNumber(String loanNumber) {
        Object loan = template.opsForValue().get(LOAN_NUMBER_KEY_PREFIX + loanNumber);
        if (loan == null) return Optional.empty();
        return findById(UUID.fromString(loan.toString()));
    }

    public Optional<Loan> findByAccountId(UUID accountId) {
        Object loan = template.opsForValue().get(LOAN_ACCOUNT_KEY_PREFIX + accountId.toString());
        if (loan == null) return Optional.empty();
        return findById(UUID.fromString(loan.toString()));
    }

    public void delete(UUID id) {
        Object loanObj = template.opsForHash().get(LOAN_HASH_KEY, id.toString());
        if (loanObj instanceof Loan loan) {
            template.delete(LOAN_NUMBER_KEY_PREFIX + loan.getLoanNumber());
            template.delete(LOAN_ACCOUNT_KEY_PREFIX + loan.getAccount().getId().toString());
        }
        template.opsForHash().delete(LOAN_HASH_KEY, id.toString());
    }
}
