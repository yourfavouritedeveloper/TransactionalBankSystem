package com.my.commandservice.service;

import com.my.commandservice.config.JwtService;
import com.my.commandservice.dto.response.UserResponse;
import com.my.commandservice.entity.User;
import com.my.commandservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    public UserResponse findById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
