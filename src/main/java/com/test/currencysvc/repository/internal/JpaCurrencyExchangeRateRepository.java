package com.test.currencysvc.repository.internal;

import com.test.currencysvc.model.CurrencyModel;
import org.springframework.data.jpa.repository.JpaRepository;

interface JpaCurrencyExchangeRateRepository extends JpaRepository<CurrencyModel, Long> {}
