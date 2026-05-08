package com.my.commandservice.exceptions;

public class LoanNotFinishedException extends RuntimeException {
    public LoanNotFinishedException(String message) {
        super(message);
    }
}
