package com.test.currencysvc.it;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.currencysvc.controller.dto.response.CreateResponse;
import com.test.currencysvc.controller.dto.response.CurrencyResponse;
import com.test.currencysvc.model.CurrencyModel;
import com.test.currencysvc.model.ExchangeRate;
import com.test.currencysvc.repository.CurrencyExchangeRateRepository;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;

public class CurrencyIntegrationTest extends BaseIntegrationTest {

  private static final String FIND_ALL_AVAILABLE = "/v1/currencies";
  private static final String FIND_RATES_BY_CODE = "/v1/currencies/{code}/rates";
  private static final String SUBSCRIBE = "/v1/currencies/{code}/subscribe";

  @Autowired private CurrencyExchangeRateRepository cacheableCurrencyExchangeRateRepository;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @SneakyThrows
  void shouldReturnCodes_whenFindExistingCodes_givenNothing() {
    // arrange
    final var resource = getResource("available-currencies-it.json");
    final var expected =
        objectMapper.readValue(resource, new TypeReference<List<CurrencyResponse>>() {});

    // act
    final var actual =
        restClient
            .get()
            .uri(
                format("http://localhost:%s", port),
                uriBuilder -> uriBuilder.path(FIND_ALL_AVAILABLE).build())
            .accept(APPLICATION_JSON)
            .retrieve()
            .body(new ParameterizedTypeReference<List<CurrencyResponse>>() {});

    // assert
    assertEquals(expected, actual);
  }

  @Test
  @SneakyThrows
  void shouldReturnId_whenSubscribe_givenCode() {
    // arrange
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody("{\"data\": {\"USD\": 2.0}}"));

    // act
    final var actual =
        restClient
            .post()
            .uri(
                format("http://localhost:%s", port),
                uriBuilder -> uriBuilder.path(SUBSCRIBE).build("USD"))
            .accept(APPLICATION_JSON)
            .retrieve()
            .body(new ParameterizedTypeReference<CreateResponse<Long>>() {});

    // assert
    final var expected =
        cacheableCurrencyExchangeRateRepository
            .findByCode(Currency.getInstance("USD"))
            .get()
            .getId();
    assertEquals(expected, actual.getId());
  }

  @Test
  @SneakyThrows
  void shouldReturnRates_whenFindRatesByCode_givenCode() {
    // arrange
    final var model = new CurrencyModel();
    model.setCode("PLN");
    model.setRates(List.of(new ExchangeRate("EUR", 2.0)));

    cacheableCurrencyExchangeRateRepository.save(model);

    // act
    final var actual =
        restClient
            .get()
            .uri(
                format("http://localhost:%s", port),
                uriBuilder -> uriBuilder.path(FIND_RATES_BY_CODE).build("PLN"))
            .accept(APPLICATION_JSON)
            .retrieve()
            .body(new ParameterizedTypeReference<Map<String, Double>>() {});

    // assert
    assertEquals(Map.of("EUR", 2.0), actual);
  }

  @SneakyThrows
  private String getResource(String path) {
    final InputStream inputStream = getClass().getResourceAsStream(format("/data/%s", path));
    return new BufferedReader(new InputStreamReader(inputStream, UTF_8))
        .lines()
        .collect(joining("\n"));
  }
}
