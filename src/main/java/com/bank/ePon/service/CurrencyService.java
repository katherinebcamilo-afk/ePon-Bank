package com.bank.ePon.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class CurrencyService {
    private final RestTemplate restTemplate = new RestTemplate();

    public Map<String, Object> getExchangeRates(String baseCurrency) {
        String url = "https://api.frankfurter.dev/v1/latest?base=" + baseCurrency;
        return restTemplate.getForObject(url, Map.class);
    }
}
