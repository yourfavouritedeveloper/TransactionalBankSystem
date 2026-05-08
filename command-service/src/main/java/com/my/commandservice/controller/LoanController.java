package com.my.commandservice.controller;

import com.my.commandservice.dto.request.LoanRequest;
import com.my.commandservice.dto.request.RestructureLoanRequest;
import com.my.commandservice.dto.response.LoanResponse;
import com.my.commandservice.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/loan")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanRequest loanRequest) {
        return ResponseEntity.ok(loanService.createLoan(loanRequest));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LoanResponse> makePayment(@PathVariable UUID id, @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(loanService.makePayment(id, amount));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LoanResponse> earlyPayment(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.earlyPayment(id));
    }

    @PatchMapping("/{id}/restructure")
    public ResponseEntity<LoanResponse> requestRestructure(@PathVariable UUID id, @RequestParam String reason) {
        return ResponseEntity.ok(loanService.requestRestructure(id,reason));
    }


}
