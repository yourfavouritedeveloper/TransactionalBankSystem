package com.my.commandservice.service;

import com.my.commandservice.config.ExchangeRateClient;
import com.my.commandservice.entity.enumeration.Currency;
import com.my.commandservice.exceptions.InvalidAmountException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final ExchangeRateClient client;

    public BigDecimal convert(Currency from,
                              Currency to,
                              BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }

        BigDecimal rate = client.getRate(from.name(), to.name());

        return amount.multiply(rate);
    }
}
