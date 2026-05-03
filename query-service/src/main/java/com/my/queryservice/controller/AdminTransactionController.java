package com.my.queryservice.controller;

import com.my.queryservice.dto.response.TransactionResponse;
import com.my.queryservice.entity.enumeration.TransactionDirection;
import com.my.queryservice.entity.enumeration.TransactionStatus;
import com.my.queryservice.entity.enumeration.TransactionType;
import com.my.queryservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/transaction")
public class AdminTransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> findAll(@RequestParam(required = false) TransactionStatus status,
                                                             @RequestParam(required = false) TransactionType type,
                                                             @RequestParam(required = false)TransactionDirection direction) {
        return ResponseEntity.ok(transactionService.findAll(status, type, direction));
    }


    @GetMapping("/schedule")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<TransactionResponse>> findAllScheduled() {
        return ResponseEntity.ok(transactionService.findAllScheduled());
    }
}
