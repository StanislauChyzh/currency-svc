package com.test.currencysvc.service;

import static java.text.MessageFormat.format;
import static java.util.Arrays.stream;

import com.test.currencysvc.client.CurrencyExchangeRateClient;
import com.test.currencysvc.client.dto.CurrencyCode;
import com.test.currencysvc.model.CurrencyModel;
import com.test.currencysvc.repository.CurrencyExchangeRateRepository;
import com.test.currencysvc.service.dto.UpdateRate;
import com.test.currencysvc.service.mapper.CurrencyExchangeRateMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class CurrencyExchangeRateService {

  private final CurrencyExchangeRateRepository cacheableCurrencyExchangeRateRepository;
  private final CurrencyExchangeRateClient freeCurrencyClient;
  private final CurrencyExchangeRateMapper mapper;

  public List<Currency> findAvailableCodes() {
    return stream(CurrencyCode.values()).map(mapper::map).toList();
  }

  @Transactional(readOnly = true)
  public List<Currency> findExistingCodes() {
    return cacheableCurrencyExchangeRateRepository.findAll().stream()
        .map(model -> mapper.map(model.getCode()))
        .toList();
  }

  @Transactional
  public Map<Currency, Double> loadRatesByCode(@NotNull Currency code) {
    final var currency = loadCurrency(code);
    return mapper.map(currency);
  }

  @Transactional
  public Long subscribe(@NotNull Currency currency) {
    final var code = mapper.map(currency);
    final var latestRates = freeCurrencyClient.findLatestRates(code);
    final var model = mapper.map(currency, latestRates);
    return cacheableCurrencyExchangeRateRepository.save(model).getId();
  }

  @Transactional
  public void update(@NotEmpty List<@Valid UpdateRate> rates) {
    rates.stream()
        .map(this::toUpdatedCurrencyModel)
        .forEach(cacheableCurrencyExchangeRateRepository::update);
  }

  private CurrencyModel toUpdatedCurrencyModel(UpdateRate rate) {
    final var model = loadCurrency(rate.currency());
    final var exchangeRates = mapper.map(rate.rates());
    model.getRates().clear();
    model.getRates().addAll(exchangeRates);
    return model;
  }

  private CurrencyModel loadCurrency(Currency code) {
    return cacheableCurrencyExchangeRateRepository
        .findByCode(code)
        .orElseThrow(() -> new RuntimeException(format("Currency {0} rates not found.", code)));
  }
}
