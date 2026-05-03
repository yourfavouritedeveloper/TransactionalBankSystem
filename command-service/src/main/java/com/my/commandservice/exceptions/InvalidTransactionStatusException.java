package com.my.commandservice.exceptions;

public class InvalidTransactionStatusException extends RuntimeException {
    public InvalidTransactionStatusException(String message) {
        super(message);
    }
}
