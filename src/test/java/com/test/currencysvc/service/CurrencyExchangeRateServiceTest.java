package com.test.currencysvc.service;

import static com.test.currencysvc.client.dto.CurrencyCode.HUF;
import static java.util.Optional.empty;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.test.currencysvc.client.CurrencyExchangeRateClient;
import com.test.currencysvc.model.CurrencyModel;
import com.test.currencysvc.model.ExchangeRate;
import com.test.currencysvc.repository.CurrencyExchangeRateRepository;
import com.test.currencysvc.service.dto.UpdateRate;
import com.test.currencysvc.service.mapper.CurrencyExchangeRateMapperImpl;
import jakarta.validation.ConstraintViolationException;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
      CurrencyExchangeRateService.class,
      MethodValidationPostProcessor.class,
      CurrencyExchangeRateMapperImpl.class
    })
class CurrencyExchangeRateServiceTest {

  @Autowired private CurrencyExchangeRateService currencyExchangeRateService;

  @MockBean private CurrencyExchangeRateRepository cacheableCurrencyExchangeRateRepository;

  @MockBean private CurrencyExchangeRateClient freeCurrencyClient;

  @Test
  void shouldReturnCodes_whenFindAvailableCodes_givenNothing() {
    // arrange
    final var expected =
        "EUR, USD, JPY, BGN, CZK, DKK, GBP, HUF, PLN, RON, SEK, CHF, ISK, NOK, HRK, RUB, TRY, AUD, BRL, "
            + "CAD, CNY, HKD, IDR, ILS, INR, KRW, MXN, MYR, NZD, PHP, SGD, THB, ZAR";

    // act
    final var actual = currencyExchangeRateService.findAvailableCodes();

    // assert
    assertEquals(expected, actual.stream().map(Currency::getCurrencyCode).collect(joining(", ")));
  }

  @Test
  void shouldReturnCodes_whenFindExistingCodes_givenNothing() {
    // arrange
    final var currency = new CurrencyModel();
    currency.setCode(HUF.name());

    when(cacheableCurrencyExchangeRateRepository.findAll()).thenReturn(List.of(currency));

    // act
    final var actual = currencyExchangeRateService.findExistingCodes();

    // assert
    assertEquals(List.of(Currency.getInstance("HUF")), actual);
  }

  @Test
  void shouldReturnRates_whenLoadRatesByCode_givenCode() {
    // arrange
    final var currency = new CurrencyModel();
    currency.setCode(HUF.name());
    currency.setRates(List.of(new ExchangeRate(HUF.name(), 2.0D)));

    when(cacheableCurrencyExchangeRateRepository.findByCode(Currency.getInstance("HUF")))
        .thenReturn(Optional.of(currency));

    // act
    final var actual = currencyExchangeRateService.loadRatesByCode(Currency.getInstance("HUF"));

    // assert
    assertEquals(Map.of(Currency.getInstance("HUF"), 2.0), actual);
  }

  @Test
  void shouldThrowRuntimeException_whenLoadRatesByCode_givenCurrencyNotExists() {
    // arrange
    when(cacheableCurrencyExchangeRateRepository.findByCode(Currency.getInstance("HUF")))
        .thenReturn(empty());

    // act

    // assert
    final var actual =
        assertThrows(
            RuntimeException.class,
            () -> currencyExchangeRateService.loadRatesByCode(Currency.getInstance("HUF")));
    assertEquals("Currency HUF rates not found.", actual.getMessage());
  }

  @Test
  void shouldThrowConstraintViolationException_whenLoadRatesByCode_givenNull() {
    // arrange

    // act

    // assert
    final var actual =
        assertThrows(
            ConstraintViolationException.class,
            () -> currencyExchangeRateService.loadRatesByCode(null));
    assertEquals("loadRatesByCode.code: must not be null", actual.getMessage());
  }

  @Test
  void shouldReturnId_whenSubscribe_givenCode() {
    // arrange
    final var currency = new CurrencyModel();
    currency.setId(1L);

    when(freeCurrencyClient.findLatestRates(HUF)).thenReturn(Map.of(HUF, 2.0));
    when(cacheableCurrencyExchangeRateRepository.save(any())).thenReturn(currency);

    // act
    final var actual = currencyExchangeRateService.subscribe(Currency.getInstance("HUF"));

    // assert
    assertEquals(1L, actual);
  }

  @Test
  void shouldThrowConstraintViolationException_whenSubscribe_givenNull() {
    // arrange

    // act

    // assert
    final var actual =
        assertThrows(
            ConstraintViolationException.class, () -> currencyExchangeRateService.subscribe(null));
    assertEquals("subscribe.currency: must not be null", actual.getMessage());
  }

  @Test
  void shouldThrowConstraintViolationException_whenUpdate_givenNull() {
    // arrange

    // act

    // assert
    final var actual =
        assertThrows(
            ConstraintViolationException.class, () -> currencyExchangeRateService.update(null));
    assertEquals("update.rates: must not be empty", actual.getMessage());
  }

  @Test
  void shouldReturnNothing_whenSubscribe_givenRates() {
    // arrange
    final var currency = new CurrencyModel();
    currency.setRates(new ArrayList<>());

    final var huf = Currency.getInstance("HUF");
    final var rate = new UpdateRate(huf, Map.of(huf, 2.0));

    when(cacheableCurrencyExchangeRateRepository.findByCode(huf)).thenReturn(Optional.of(currency));
    doNothing().when(cacheableCurrencyExchangeRateRepository).update(currency);

    // act
    currencyExchangeRateService.update(List.of(rate));

    // assert
    verify(cacheableCurrencyExchangeRateRepository).update(currency);
  }
}
