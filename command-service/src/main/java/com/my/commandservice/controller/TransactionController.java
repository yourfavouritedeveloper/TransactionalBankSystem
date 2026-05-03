package com.my.commandservice.controller;

import ch.qos.logback.core.util.Loader;
import com.my.commandservice.dto.response.TransactionResponse;
import com.my.commandservice.entity.enumeration.Currency;
import com.my.commandservice.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public ResponseEntity<TransactionResponse> transfer(@RequestParam UUID fromId,
                                                     @RequestParam(required = false) UUID toId,
                                                     @RequestParam(required = false) String toIban,
                                                     @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(transactionService.transfer(fromId,toId,toIban,amount));
    }

    @PostMapping("/refund")
    public ResponseEntity<TransactionResponse> refund(@RequestParam UUID id) {
        return ResponseEntity.ok(transactionService.refund(id));
    }

    @PostMapping("/fee")
    public ResponseEntity<TransactionResponse> fee(@RequestParam UUID fromId,
                                                                @RequestParam BigDecimal amount,
                                                                @RequestParam UUID toId) {
        return ResponseEntity.ok(transactionService.fee(fromId,amount,toId));
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

    @PostMapping("/schedule")
    public ResponseEntity<TransactionResponse> schedule(@RequestParam UUID fromId,
                                                        @RequestParam BigDecimal amount,
                                                        @RequestParam UUID toId,
                                                        @RequestParam LocalDateTime scheduledAt) {
        return ResponseEntity.ok(transactionService.schedule(fromId,toId,amount,scheduledAt));
    }

    @PatchMapping("/schedule/{id}")
    public ResponseEntity<TransactionResponse> changeSchedule(@PathVariable UUID id, @RequestParam LocalDateTime scheduledAt) {
        return ResponseEntity.ok(transactionService.changeScheduleTime(id, scheduledAt));
    }

    @PutMapping("/schedule/{id}")
    public ResponseEntity<TransactionResponse> cancelSchedule(@PathVariable UUID id) {
        return ResponseEntity.ok(transactionService.cancelSchedule(id));
    }

}
