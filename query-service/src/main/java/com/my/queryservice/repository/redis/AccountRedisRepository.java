package com.my.queryservice.repository.redis;

import com.my.queryservice.entity.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AccountRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String ACCOUNT_HASH_KEY = "query:account";
    private static final String IBAN_KEY_PREFIX = "query:account:iban:";
    private static final String USER_KEY_PREFIX = "query:account:user:";

    public Account save(Account account) {
        template.opsForHash().put(ACCOUNT_HASH_KEY, account.getId().toString(), account);
        template.opsForValue().set(IBAN_KEY_PREFIX + account.getIban(), account.getId().toString());
        template.opsForValue().set(USER_KEY_PREFIX + account.getUser().getId().toString(), account.getId().toString());
        return account;
    }

    public List<Account> findAll() {
        return template.opsForHash()
                .values(ACCOUNT_HASH_KEY)
                .stream()
                .map(obj -> obj instanceof Account a ? a : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Optional<Account> findById(UUID id) {
        Object account = template.opsForHash().get(ACCOUNT_HASH_KEY, id.toString());
        return Optional.ofNullable(account instanceof Account a ? a : null);
    }

    public Optional<Account> findByIban(String iban) {
        Object accountId = template.opsForValue().get(IBAN_KEY_PREFIX + iban);
        if (accountId == null) return Optional.empty();
        return findById(UUID.fromString(accountId.toString()));
    }

    public Optional<Account> findByUserId(UUID userId) {
        Object accountId = template.opsForValue().get(USER_KEY_PREFIX + userId.toString());
        if (accountId == null) return Optional.empty();
        return findById(UUID.fromString(accountId.toString()));
    }

    public void delete(UUID id) {
        Object accountObj = template.opsForHash().get(ACCOUNT_HASH_KEY, id.toString());
        if (accountObj instanceof Account account) {
            template.delete(IBAN_KEY_PREFIX + account.getIban());
            template.delete(USER_KEY_PREFIX + account.getUser().getId().toString());
        }
        template.opsForHash().delete(ACCOUNT_HASH_KEY, id.toString());
    }
}