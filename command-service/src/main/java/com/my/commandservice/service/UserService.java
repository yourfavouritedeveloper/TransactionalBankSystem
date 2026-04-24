package com.my.commandservice.service;

import com.my.commandservice.config.JwtService;
import com.my.commandservice.dto.request.LoginRequest;
import com.my.commandservice.dto.request.RegisterRequest;
import com.my.commandservice.dto.request.UserRequest;
import com.my.commandservice.dto.response.AuthResponse;
import com.my.commandservice.dto.response.UserResponse;
import com.my.commandservice.entity.BlackList;
import com.my.commandservice.entity.RefreshToken;
import com.my.commandservice.entity.User;
import com.my.commandservice.entity.enumeration.Role;
import com.my.commandservice.exceptions.InvalidValidationException;
import com.my.commandservice.exceptions.PasswordDoesNotMatchException;
import com.my.commandservice.exceptions.RefreshTokenNotFoundException;
import com.my.commandservice.exceptions.UserNotFoundException;
import com.my.commandservice.repository.BlackListRepository;
import com.my.commandservice.repository.RefreshTokenRepository;
import com.my.commandservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;



    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        User user = toUser(registerRequest);
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .role(user.getRole())
                .build();

        refreshTokenRepository.save(token);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Transactional
    public AuthResponse authenticate(LoginRequest loginRequest) {
        User user = (User) userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with given username does not exist"));

        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new PasswordDoesNotMatchException("Password does not match");
        }

        if(refreshTokenRepository.findByUser(user)
                .orElse(null) != null
        ) {
            //todo: Add a logic that can log user about incoming danger of login from different device (like google)
            throw new UserNotFoundException("More than 1 login in different devices is not being supported yet!");
        }

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .user(user)
                .role(user.getRole())
                .build();

        refreshTokenRepository.save(token);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    @Transactional
    public UserResponse update(UUID id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(userRequest.getUsername() != null) user.setUsername(userRequest.getUsername());
        if(userRequest.getEmail() != null) user.setEmail(userRequest.getEmail());
        if(userRequest.getPassword() != null) user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        if(userRequest.getFullName() != null) user.setFullName(userRequest.getFullName());
        if(userRequest.getRole() != null) user.setRole(userRequest.getRole());

        userRepository.save(user);
        return toUserResponse(user);
    }


    @Transactional
    public void delete(UUID id) {
        userRepository.deleteById(id);
    }



    @Transactional
    public void logout(String token) {

        String username = jwtService.extractUsername(token);

        User user = (User) userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!jwtService.validateToken(token, user)) {
            throw new InvalidValidationException("Invalid token");
        }

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token with provided user does not exist"));

        refreshTokenRepository.delete(refreshToken);

        BlackList blackList = BlackList.builder()
                .user(user)
                .token(token)
                .build();

        blackListRepository.save(blackList);

    }


    @Transactional
    public AuthResponse refresh(String token) {

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(token)
                .orElseThrow(() -> new RefreshTokenNotFoundException("Refresh token not found"));


        User user = refreshToken.getUser();

        if (!jwtService.validateToken(token, user)) {
            throw new InvalidValidationException("Invalid token");
        }

        refreshTokenRepository.delete(refreshToken);

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        RefreshToken newToken = RefreshToken.builder()
                .refreshToken(newRefreshToken)
                .user(user)
                .role(user.getRole())
                .build();

        refreshTokenRepository.save(newToken);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();


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

    public User toUser(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .fullName(registerRequest.getFullName())
                .email(registerRequest.getEmail())
                .role(Role.USER)
                .build();
    }
}
