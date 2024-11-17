package com.test.currencysvc.client.dto;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.test.free-currency")
public record FreeCurrencyProperties(
    String baseUrl, String apiAccessKey, String findLatestRatesPath) {}
