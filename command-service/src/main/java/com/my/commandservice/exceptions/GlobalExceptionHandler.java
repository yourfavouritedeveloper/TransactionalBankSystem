package com.my.commandservice.exceptions;

import com.my.commandservice.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public ResponseEntity<ErrorResponse> handlePasswordDoesNotMatchException(PasswordDoesNotMatchException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(InvalidValidationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidValidationException(InvalidValidationException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundException(AccountNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleTransactionNotFoundException(TransactionNotFoundException e) {
        return buildResponse(e.getMessage(), 404);
    }

    @ExceptionHandler(InvalidAccountStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountStatusException(InvalidAccountStatusException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(InvalidAccountTypeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAccountTypeException(InvalidAccountTypeException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmountException(InvalidAmountException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(InvalidTransactionStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTransactionStatusException(InvalidTransactionStatusException e) {
        return buildResponse(e.getMessage(), 400);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return buildResponse("Internal server error", 500);
    }

    private ResponseEntity<ErrorResponse> buildResponse(String message, int status) {
        return ResponseEntity
                .status(status)
                .body(new ErrorResponse(message, status));
    }
}
