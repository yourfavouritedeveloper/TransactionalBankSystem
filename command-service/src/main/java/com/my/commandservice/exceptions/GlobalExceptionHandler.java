package com.my.commandservice.exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(PasswordDoesNotMatchException.class)
    public String handlePasswordDoesNotMatchException(PasswordDoesNotMatchException e) {
        return e.getMessage();
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    public String handleRefreshTokenNotFoundException(RefreshTokenNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(InvalidValidationException.class)
    public String handleInvalidValidationException(InvalidValidationException e) {
        return e.getMessage();
    }

}
