package com.my.commandservice.controller;

import com.my.commandservice.dto.request.LoanRequest;
import com.my.commandservice.dto.request.RestructureLoanRequest;
import com.my.commandservice.dto.request.UpdateLoanRequest;
import com.my.commandservice.dto.response.LoanResponse;
import com.my.commandservice.entity.enumeration.LoanStatus;
import com.my.commandservice.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/loan")
@RequiredArgsConstructor
public class AdminLoanController {

    private final LoanService loanService;

    @PatchMapping("/{id}/restructure")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> restructureLoan(@PathVariable UUID id,@Valid @RequestBody RestructureLoanRequest loanRequest) {
        return ResponseEntity.ok(loanService.applyRestructureRequest(id,loanRequest));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> update(@PathVariable UUID id,@Valid @RequestBody UpdateLoanRequest loanRequest) {
        return ResponseEntity.ok(loanService.update(id,loanRequest));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> updateStatus(@PathVariable UUID id,@RequestParam LoanStatus status) {
        return ResponseEntity.ok(loanService.updateStatus(id,status));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LoanResponse> close(@PathVariable UUID id) {
        return ResponseEntity.ok(loanService.close(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        loanService.delete(id);
        return ResponseEntity.ok("Loan has been deleted");
    }
}
