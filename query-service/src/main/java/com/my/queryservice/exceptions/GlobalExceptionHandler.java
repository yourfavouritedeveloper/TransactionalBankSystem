package com.my.queryservice.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFoundException(UserNotFoundException e) {
        return e.getMessage();
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public String handleAccountNotFoundException(AccountNotFoundException e) {
        return e.getMessage();
    }


}
