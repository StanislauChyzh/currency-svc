package com.test.currencysvc.client.internal;

import static com.test.currencysvc.client.dto.CurrencyCode.AUD;
import static com.test.currencysvc.client.dto.CurrencyCode.USD;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.test.currencysvc.client.CurrencyExchangeRateClient;
import com.test.currencysvc.client.dto.CurrencyCode;
import com.test.currencysvc.client.dto.FreeCurrencyProperties;
import com.test.currencysvc.configuration.RestClientConfig;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.Map;
import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties({FreeCurrencyProperties.class})
@ContextConfiguration(
    classes = {
      RestClientConfig.class,
      FreeCurrencyClient.class,
      MethodValidationPostProcessor.class
    })
@TestPropertySource("classpath:application-test.properties")
class FreeCurrencyClientTest {

  @Autowired private CurrencyExchangeRateClient freeCurrencyClient;

  @Autowired private FreeCurrencyProperties properties;

  private MockWebServer mockWebServer;

  @BeforeEach
  void setUp() throws IOException {
    mockWebServer = new MockWebServer();
    mockWebServer.start(8567);
  }

  @AfterEach
  void tearDown() throws IOException {
    mockWebServer.shutdown();
  }

  @Test
  @SneakyThrows
  void shouldReturnRates_whenFindLatestRates_givenCode() {
    // arrange
    mockWebServer.enqueue(
        new MockResponse()
            .setHeader("Content-Type", "application/json")
            .setBody("{\"data\": {\"USD\": 2.0}}"));

    // act
    final Map<CurrencyCode, Double> actual = freeCurrencyClient.findLatestRates(AUD);

    // assert
    final RecordedRequest request = mockWebServer.takeRequest();

    assertEquals("GET", request.getMethod());
    assertEquals(
        format(
            "%s%s?apikey=%s&base_currency=%s",
            properties.baseUrl(), properties.findLatestRatesPath(), properties.apiAccessKey(), AUD),
        request.getRequestUrl().toString());
    assertEquals(Map.of(USD, 2.0), actual);
  }

  @Test
  void shouldThrowRuntimeException_whenFindLatestRates_givenRatesNotFound() {
    // arrange
    mockWebServer.enqueue(new MockResponse().setHeader("Content-Type", "application/json"));

    // act

    // assert
    final var actual =
        assertThrows(RuntimeException.class, () -> freeCurrencyClient.findLatestRates(AUD));
    assertEquals("Can not find exchange rates for AUD", actual.getMessage());
  }

  @Test
  void shouldThrowConstraintViolationException_whenFindLatestRates_givenNull() {
    // arrange

    // act

    // assert
    final var actual =
        assertThrows(
            ConstraintViolationException.class, () -> freeCurrencyClient.findLatestRates(null));
    assertEquals("findLatestRates.code: must not be null", actual.getMessage());
  }
}
