package com.my.commandservice.controller;

import com.my.commandservice.dto.request.LoginRequest;
import com.my.commandservice.dto.request.RegisterRequest;
import com.my.commandservice.dto.response.AuthResponse;
import com.my.commandservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;


    @PostMapping
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(userService.register(registerRequest));
    }

    @PutMapping
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(userService.authenticate(loginRequest));
    }

    @PutMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        token = token.substring(7);
        userService.logout(token);
        return ResponseEntity.ok("Successfully logged out");
    }

    @PutMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestParam String token) {
        return ResponseEntity.ok(userService.refresh(token));
    }
}
