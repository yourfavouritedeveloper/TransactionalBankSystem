package com.my.commandservice.controller;

import com.my.commandservice.dto.request.UpdateAccountRequest;
import com.my.commandservice.dto.response.AccountResponse;
import com.my.commandservice.entity.enumeration.AccountStatus;
import com.my.commandservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/account")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AccountService accountService;

    @PutMapping("/status/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> updateStatus(@PathVariable("id") UUID id, @RequestParam AccountStatus status) {
        return ResponseEntity.ok(accountService.updateStatus(id, status));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AccountResponse> updateStatus(@PathVariable("id") UUID id, @Valid @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.update(id, request));
    }
}
