package com.my.commandservice.config;

import com.my.commandservice.dto.response.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeRateClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${exchange.api-key}")
    private String apiKey;

    public BigDecimal getRate(String from, String to) {

        String url = "https://api.apilayer.com/exchangerates_data/latest"
                + "?base=" + from
                + "&symbols=" + to;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", apiKey);

        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<ExchangeRateResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        request,
                        ExchangeRateResponse.class
                );

        ExchangeRateResponse body = response.getBody();

        if (body == null || !body.isSuccess()) {
            throw new RuntimeException("Failed to fetch exchange rate");
        }

        return body.getRates().get(to);
    }
}
