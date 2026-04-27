package com.my.queryservice.service;


import com.my.queryservice.dto.response.UserResponse;
import com.my.queryservice.entity.User;
import com.my.queryservice.exceptions.UserNotFoundException;
import com.my.queryservice.repository.redis.UserRedisRepository;
import com.my.queryservice.repository.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserRedisRepository userRedisRepository;


    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = userRedisRepository.findById(id).orElseGet(() -> {
            User entity = userRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            userRedisRepository.save(entity);

            return entity;
        });

        return toUserResponse(user);

    }

    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = userRedisRepository.findByUsername(username).orElseGet(() -> {
            User entity = (User) userRepository.findByUsername(username)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            userRedisRepository.save(entity);

            return entity;
        });

        return toUserResponse(user);

    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> cached = userRedisRepository.findAll();

        if (!cached.isEmpty()) {
            return cached.stream().map(this::toUserResponse).toList();
        }

        List<User> entities = userRepository.findAll();
        entities.forEach(userRedisRepository::save);

        return entities.stream().map(this::toUserResponse).toList();
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
