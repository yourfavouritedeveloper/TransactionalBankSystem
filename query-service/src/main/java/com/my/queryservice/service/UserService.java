package com.my.queryservice.service;


import com.my.queryservice.dto.response.UserResponse;
import com.my.queryservice.entity.User;
import com.my.queryservice.exceptions.UserNotFoundException;
import com.my.queryservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    @Transactional(readOnly = true)
    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse findByUsername(String username) {
        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll() {
        List<User> users = userRepository.findAll();

        return users.stream().map(this::toUserResponse).collect(Collectors.toList());
    }



    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .build();
    }


}
