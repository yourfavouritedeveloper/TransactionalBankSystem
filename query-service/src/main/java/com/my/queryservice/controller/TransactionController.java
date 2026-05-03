package com.my.queryservice.controller;

import com.my.queryservice.dto.response.TransactionResponse;
import com.my.queryservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transaction")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/id/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(transactionService.findById(id));
    }

    @GetMapping("/from/{id}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByFromAccount(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(transactionService.findByFromAccount(id));
    }

    @GetMapping("/to/{id}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByToAccount(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(transactionService.findByToAccount(id));
    }
}
