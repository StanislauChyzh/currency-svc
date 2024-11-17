package com.test.currencysvc.controller;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.stream.Collectors.joining;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.currencysvc.controller.dto.response.CreateResponse;
import com.test.currencysvc.controller.dto.response.CurrencyResponse;
import com.test.currencysvc.service.CurrencyExchangeRateService;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {MethodValidationPostProcessor.class, CurrencyController.class})
class CurrencyControllerTest {

  private static final String FIND_ALL_AVAILABLE = "/v1/currencies";
  private static final String FIND_RATES_BY_CODE = "/v1/currencies/{code}/rates";
  private static final String SUBSCRIBE = "/v1/currencies/{code}/subscribe";

  @MockBean private CurrencyExchangeRateService currencyExchangeRateService;
  private final ObjectMapper objectMapper = new ObjectMapper();

  private WebTestClient webTestClient;

  @PostConstruct
  public void setup() {
    final var controller = new CurrencyController(currencyExchangeRateService);
    this.webTestClient = MockMvcWebTestClient.bindToController(controller).build();
  }

  @Test
  @SneakyThrows
  void shouldReturnCollectionOfCurrencyResponses_whenFindAllAvailable_givenNothing() {
    // arrange
    final var resource = getResource("available-currencies.json");
    final var expected =
        objectMapper.readValue(resource, new TypeReference<List<CurrencyResponse>>() {});

    when(currencyExchangeRateService.findAvailableCodes())
        .thenReturn(List.of(Currency.getInstance("USD")));

    // act
    final var response =
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(FIND_ALL_AVAILABLE).build())
            .accept(APPLICATION_JSON)
            .exchange();

    // assert
    response.expectStatus().isOk();
    response
        .expectBody(new ParameterizedTypeReference<List<CurrencyResponse>>() {})
        .isEqualTo(expected);
  }

  @Test
  @SneakyThrows
  void shouldReturnRates_whenFindRatesByCode_givenCode() {
    // arrange
    final var code = Currency.getInstance("USD");
    final var eur = Currency.getInstance("EUR");

    final var resource = getResource("available-currencies.json");
    final var expected =
        objectMapper.readValue(resource, new TypeReference<List<CurrencyResponse>>() {});

    when(currencyExchangeRateService.loadRatesByCode(code)).thenReturn(Map.of(eur, 1.0));

    // act
    final var response =
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(FIND_RATES_BY_CODE).build(code))
            .accept(APPLICATION_JSON)
            .exchange();

    // assert
    response.expectStatus().isOk();
    response
        .expectBody(new ParameterizedTypeReference<Map<Currency, Double>>() {})
        .isEqualTo(Map.of(eur, 1.0));
  }

  @Test
  @SneakyThrows
  void shouldReturn400Status_whenFindRatesByCode_givenIncorrectCode() {
    // arrange

    // act
    final var response =
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(FIND_RATES_BY_CODE).build("code"))
            .accept(APPLICATION_JSON)
            .exchange();

    // assert
    response.expectStatus().is4xxClientError();
  }

  @Test
  @SneakyThrows
  void shouldReturnId_whenSubscribe_givenCode() {
    // arrange
    final var code = Currency.getInstance("USD");

    when(currencyExchangeRateService.subscribe(code)).thenReturn(1L);

    // act
    final var response =
        webTestClient
            .post()
            .uri(uriBuilder -> uriBuilder.path(SUBSCRIBE).build(code))
            .accept(APPLICATION_JSON)
            .exchange();

    // assert
    response.expectStatus().isOk();
    response
        .expectBody(new ParameterizedTypeReference<CreateResponse<Long>>() {})
        .isEqualTo(new CreateResponse<>(1L));
  }

  @Test
  @SneakyThrows
  void shouldReturn400Status_whenSubscribe_givenIncorrectCode() {
    // arrange

    // act
    final var response =
        webTestClient
            .get()
            .uri(uriBuilder -> uriBuilder.path(SUBSCRIBE).build("code"))
            .accept(APPLICATION_JSON)
            .exchange();

    // assert
    response.expectStatus().is4xxClientError();
  }

  @SneakyThrows
  private String getResource(String path) {
    final InputStream inputStream = getClass().getResourceAsStream(format("/data/%s", path));
    return new BufferedReader(new InputStreamReader(inputStream, UTF_8))
        .lines()
        .collect(joining("\n"));
  }
}
