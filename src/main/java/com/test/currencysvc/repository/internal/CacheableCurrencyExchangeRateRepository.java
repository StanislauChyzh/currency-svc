package com.test.currencysvc.repository.internal;

import com.test.currencysvc.model.CurrencyModel;
import com.test.currencysvc.repository.CurrencyExchangeRateRepository;
import java.util.Currency;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class CacheableCurrencyExchangeRateRepository implements CurrencyExchangeRateRepository {

  private final JpaCurrencyExchangeRateRepository jpaRepository;

  @Override
  @Cacheable("rates")
  public Optional<CurrencyModel> findByCode(Currency code) {
    final var model = new CurrencyModel();
    model.setCode(code.getCurrencyCode());
    return jpaRepository.findOne(Example.of(model));
  }

  @Override
  public List<CurrencyModel> findAll() {
    return jpaRepository.findAll();
  }

  @Override
  @CachePut(value = "rates", key = "#model.code")
  public CurrencyModel save(CurrencyModel model) {
    return jpaRepository.save(model);
  }

  @Override
  @CachePut(value = "rates", key = "#model.code")
  public void update(CurrencyModel model) {
    jpaRepository.save(model);
  }
}
