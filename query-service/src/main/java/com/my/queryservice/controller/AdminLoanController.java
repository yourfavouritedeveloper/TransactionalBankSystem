package com.my.queryservice.controller;

import com.my.queryservice.dto.response.LoanResponse;
import com.my.queryservice.entity.enumeration.LoanStatus;
import com.my.queryservice.entity.enumeration.LoanType;
import com.my.queryservice.service.LoanService;
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
@RequestMapping("/api/admin/loan")
public class AdminLoanController {

    private final LoanService loanService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LoanResponse>> findAll(@RequestParam(required = false) LoanStatus status,
                                                      @RequestParam(required = false) LoanType type) {
        return ResponseEntity.ok(loanService.findAll(status,type));
    }
}
