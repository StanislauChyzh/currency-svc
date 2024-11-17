package com.test.currencysvc.client;

import com.test.currencysvc.client.dto.CurrencyCode;
import jakarta.validation.constraints.NotNull;
import java.util.Map;

/** Client is delivering exchanging rates data. */
public interface CurrencyExchangeRateClient {

  /**
   * Finds latest exchange rates.
   *
   * @param code code of source currency
   * @return map of rates where key is currency code and value is rate.
   */
  Map<CurrencyCode, Double> findLatestRates(@NotNull CurrencyCode code);
}
