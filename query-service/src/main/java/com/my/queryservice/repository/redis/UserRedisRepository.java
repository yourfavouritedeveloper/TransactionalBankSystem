package com.my.queryservice.repository.redis;

import com.my.queryservice.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRedisRepository {

    private final RedisTemplate<String, Object> template;

    private static final String USER_HASH_KEY = "query:user";
    private static final String USERNAME_KEY_PREFIX = "query:user:username:";

    public User save(User user) {
        template.opsForHash().put(USER_HASH_KEY, user.getId().toString(), user);

        template.opsForValue().set(
                USERNAME_KEY_PREFIX + user.getUsername(),
                user.getId().toString()
        );

        return user;
    }

    public List<User> findAll() {
        return template.opsForHash()
                .values(USER_HASH_KEY)
                .stream()
                .map(obj -> (User) obj)
                .collect(Collectors.toList());
    }

    public Optional<User> findById(UUID id) {
        Object user = template.opsForHash().get(USER_HASH_KEY, id.toString());
        return Optional.ofNullable((User) user);
    }

    public Optional<User> findByUsername(String username) {
        Object userId = template.opsForValue()
                .get(USERNAME_KEY_PREFIX + username);

        if (userId == null) {
            return Optional.empty();
        }

        return findById(UUID.fromString(userId.toString()));
    }

    public void delete(UUID id) {
        Object userObj = template.opsForHash().get(USER_HASH_KEY, id.toString());

        if (userObj != null) {
            User user = (User) userObj;
            template.delete(USERNAME_KEY_PREFIX + user.getUsername());
        }

        template.opsForHash().delete(USER_HASH_KEY, id.toString());
    }
}