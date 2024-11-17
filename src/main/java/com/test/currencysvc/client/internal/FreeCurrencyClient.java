package com.test.currencysvc.client.internal;

import static java.text.MessageFormat.*;
import static java.util.Optional.ofNullable;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.test.currencysvc.client.CurrencyExchangeRateClient;
import com.test.currencysvc.client.dto.*;
import jakarta.validation.constraints.NotNull;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClient;

@Validated
@Component
@RequiredArgsConstructor
class FreeCurrencyClient implements CurrencyExchangeRateClient {

  @Qualifier("externalRestClient")
  private final RestClient restClient;

  private final FreeCurrencyProperties properties;

  @Override
  public Map<CurrencyCode, Double> findLatestRates(@NotNull CurrencyCode code) {
    return ofNullable(
            restClient
                .get()
                .uri(
                    properties.baseUrl(),
                    uriBuilder ->
                        uriBuilder
                            .path(properties.findLatestRatesPath())
                            .queryParam("apikey", properties.apiAccessKey())
                            .queryParam("base_currency", code)
                            .build())
                .headers(httpHeaders -> httpHeaders.setContentType(APPLICATION_JSON))
                .retrieve()
                .onStatus(new ErrorHandler("Error communicating with Freecurrency service"))
                .body(RateResponse.class))
        .map(RateResponse::data)
        .orElseThrow(
            () -> new RuntimeException(format("Can not find exchange rates for {0}", code)));
  }

  private record RateResponse(Map<CurrencyCode, Double> data) {}
}
