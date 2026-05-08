package com.my.queryservice.controller;

import com.my.queryservice.dto.response.LoanResponse;
import com.my.queryservice.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/loan")
public class LoanController {

    private final LoanService loanService;

    @GetMapping("/id/{id}")
    public ResponseEntity<LoanResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<LoanResponse> findByAccountId(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.findByAccount(id));
    }

    @GetMapping("/loan-number/{loanNumber}")
    public ResponseEntity<LoanResponse> findByIban(@PathVariable String loanNumber) {
        return ResponseEntity.ok(loanService.findByLoanNumber(loanNumber));
    }
}
