package com.my.commandservice.exceptions;

public class InvalidValidationException extends RuntimeException {
    public InvalidValidationException(String message) {
        super(message);
    }
}
