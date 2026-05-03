package com.my.commandservice.controller;


import com.my.commandservice.dto.request.TransactionRequest;
import com.my.commandservice.dto.request.UpdateTransactionRequest;
import com.my.commandservice.dto.response.TransactionResponse;
import com.my.commandservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/transaction")
@RequiredArgsConstructor
public class AdminTransactionController {

    private final TransactionService transactionService;


    @PostMapping("/reversal/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> reversal(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok(transactionService.reversal(id, reason));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TransactionResponse> update(@PathVariable UUID id, @RequestBody UpdateTransactionRequest transactionRequest) {

        return ResponseEntity.ok(transactionService.update(id, transactionRequest));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        transactionService.delete(id);
        return ResponseEntity.ok("Transaction has been deleted successfully");
    }
}
