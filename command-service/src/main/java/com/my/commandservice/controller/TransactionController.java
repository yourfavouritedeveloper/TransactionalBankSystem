package com.my.commandservice.controller;

import com.my.commandservice.dto.response.TransactionResponse;
import com.my.commandservice.dto.response.TransferResponse;
import com.my.commandservice.entity.enumeration.Currency;
import com.my.commandservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestParam(required = false) UUID userId,
                                                       @RequestParam(required = false) String iban,
                                                       @RequestParam BigDecimal amount,
                                                       @RequestParam Currency currency) {
        return ResponseEntity.ok(transactionService.deposit(iban,userId,amount,currency));
    }

    @PostMapping("/withdrawal")
    public ResponseEntity<TransactionResponse> withdrawal(@RequestParam UUID userId,
                                                          @RequestParam BigDecimal amount,
                                                          @RequestParam Currency currency) {
        return ResponseEntity.ok(transactionService.withdraw(userId, amount, currency));
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponse> transfer(@RequestParam UUID fromId,
                                                     @RequestParam(required = false) UUID toId,
                                                     @RequestParam(required = false) String toIban,
                                                     @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(transactionService.transfer(fromId,toId,toIban,amount));
    }

    @PostMapping("/refund")
    public ResponseEntity<TransferResponse> refund(@RequestParam UUID id) {
        return ResponseEntity.ok(transactionService.refund(id));
    }

    @PostMapping("/hold")
    public ResponseEntity<TransactionResponse> hold(@RequestParam UUID userId,
                                                 @RequestParam BigDecimal amount,
                                                 @RequestParam Currency currency) {
        return ResponseEntity.ok(transactionService.hold(userId, amount, currency));
    }

    @PostMapping("/release")
    public ResponseEntity<TransactionResponse> release(@RequestParam UUID id) {
        return ResponseEntity.ok(transactionService.release(id));
    }

}
