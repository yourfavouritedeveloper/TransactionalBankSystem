package com.my.commandservice.controller;

import com.my.commandservice.dto.request.AccountRequest;
import com.my.commandservice.dto.response.AccountResponse;
import com.my.commandservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody AccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.createAccount(accountRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> closeAccount(@PathVariable UUID id) {
        accountService.closeAccount(id);
        return ResponseEntity.ok("Account closed");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAccount(@PathVariable UUID id) {
        accountService.deleteAccount(id);
        return ResponseEntity.ok("Account deleted");
    }
}
