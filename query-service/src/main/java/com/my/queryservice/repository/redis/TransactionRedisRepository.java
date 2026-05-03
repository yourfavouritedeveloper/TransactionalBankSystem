package com.my.queryservice.repository.redis;

import com.my.queryservice.entity.Transaction;
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
public class TransactionRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String TRANSACTION_HASH_KEY = "query:transaction";
    private static final String FROM_KEY_PREFIX = "query:transaction:from:";
    private static final String TO_KEY_PREFIX = "query:transaction:to:";

    public Transaction save(Transaction transaction) {
        template.opsForHash().put(TRANSACTION_HASH_KEY, transaction.getId().toString(), transaction);
        template.opsForValue().set(FROM_KEY_PREFIX + transaction.getFromAccount().getId().toString(), transaction.getId().toString());
        template.opsForValue().set(TO_KEY_PREFIX + transaction.getToAccount().getId().toString(), transaction.getId().toString());
        return transaction;
    }

    public List<Transaction> findAll() {
        return template.opsForHash()
                .values(TRANSACTION_HASH_KEY)
                .stream()
                .map(obj -> obj instanceof Transaction t ? t : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Transaction> findById(UUID id) {
        Object account = template.opsForHash().get(TRANSACTION_HASH_KEY, id.toString());
        return Optional.ofNullable(account instanceof Transaction t ? t : null);
    }

    public List<Transaction> findByFromAccount(UUID fromId) {
        return getTransactionsByKey(FROM_KEY_PREFIX + fromId);
    }

    public List<Transaction> findByToAccount(UUID toId) {
        return getTransactionsByKey(TO_KEY_PREFIX + toId);
    }

    private List<Transaction> getTransactionsByKey(String key) {
        List<Object> transactionIds = template.opsForList().range(key, 0, -1);
        if (transactionIds == null || transactionIds.isEmpty()) return List.of();

        return transactionIds.stream()
                .map(id -> findById(UUID.fromString(id.toString())))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        Object transactionObj = template.opsForHash().get(TRANSACTION_HASH_KEY, id.toString());
        if (transactionObj instanceof Transaction transaction) {
            template.delete(FROM_KEY_PREFIX + transaction.getFromAccount().getId().toString());
            template.delete(TO_KEY_PREFIX + transaction.getToAccount().getId().toString());
        }
        template.opsForHash().delete(TRANSACTION_HASH_KEY, id.toString());
    }
}
