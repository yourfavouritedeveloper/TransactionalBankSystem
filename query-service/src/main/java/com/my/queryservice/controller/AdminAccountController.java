package com.my.queryservice.controller;

import com.my.queryservice.dto.response.AccountResponse;
import com.my.queryservice.entity.enumeration.AccountStatus;
import com.my.queryservice.entity.enumeration.AccountType;
import com.my.queryservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/account")
public class AdminAccountController {

    private final AccountService accountService;


    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AccountResponse>> findAll(@RequestParam(required = false) AccountStatus status,
                                                         @RequestParam(required = false) AccountType type) {
        return ResponseEntity.ok(accountService.findAll(status,type));
    }
}
