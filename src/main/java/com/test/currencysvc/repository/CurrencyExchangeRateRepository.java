package com.test.currencysvc.repository;

import com.test.currencysvc.model.CurrencyModel;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

/** Repository provides methods to manage currency exchange rates. */
public interface CurrencyExchangeRateRepository {

  /**
   * Finds currency.
   *
   * @param code code of currency
   * @return optional of currency.
   */
  Optional<CurrencyModel> findByCode(Currency code);

  /**
   * Finds all currency.
   *
   * @return collection of currencies.
   */
  List<CurrencyModel> findAll();

  /**
   * Saves currency.
   *
   * @param model model of currency
   * @return saved currency.
   */
  CurrencyModel save(CurrencyModel model);

  /**
   * Updates currency.
   *
   * @param model model of currency
   */
  void update(CurrencyModel model);
}
