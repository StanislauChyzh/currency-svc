package com.test.currencysvc.job;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

import com.test.currencysvc.client.CurrencyExchangeRateClient;
import com.test.currencysvc.client.dto.CurrencyCode;
import com.test.currencysvc.service.CurrencyExchangeRateService;
import com.test.currencysvc.service.dto.UpdateRate;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SyncExchangeRateJob {

  private final CurrencyExchangeRateService currencyExchangeRateService;
  private final CurrencyExchangeRateClient freeCurrencyClient;

  @Scheduled(fixedRate = 60 * 60 * 1000)
  public void sync() {
    log.info("Syncing exchange rates.");
    final var rates = findRates();
    if (isEmpty(rates)) {
      log.info("Stopped, there are not currencies ib DB.");
      return;
    }
    currencyExchangeRateService.update(rates);
  }

  private List<UpdateRate> findRates() {
    return currencyExchangeRateService.findExistingCodes().stream()
        .map(this::toUpdateRate)
        .toList();
  }

  private UpdateRate toUpdateRate(Currency currency) {
    final var code = CurrencyCode.valueOf(currency.getCurrencyCode());
    Map<Currency, Double> latestRates = findRates(code);
    return new UpdateRate(currency, latestRates);
  }

  private Map<Currency, Double> findRates(CurrencyCode code) {
    return freeCurrencyClient.findLatestRates(code).entrySet().stream()
        .collect(toMap(entry -> Currency.getInstance(entry.getKey().name()), Map.Entry::getValue));
  }
}
