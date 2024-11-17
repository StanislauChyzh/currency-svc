package com.test.currencysvc.service.mapper;

import static java.util.stream.Collectors.toMap;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.IGNORE;

import com.test.currencysvc.client.dto.CurrencyCode;
import com.test.currencysvc.model.CurrencyModel;
import com.test.currencysvc.model.ExchangeRate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.validation.annotation.Validated;

@Validated
@Mapper(componentModel = SPRING, unmappedTargetPolicy = IGNORE)
public abstract class CurrencyExchangeRateMapper {

  public List<ExchangeRate> map(@NotEmpty Map<Currency, Double> rates) {
    return rates.entrySet().stream().map(entry -> map(entry.getKey(), entry.getValue())).toList();
  }

  public Currency map(@NotNull CurrencyCode code) {
    return Currency.getInstance(code.name());
  }

  public CurrencyCode map(@NotNull Currency code) {
    return CurrencyCode.valueOf(code.getCurrencyCode());
  }

  public Currency map(@NotBlank String code) {
    return Currency.getInstance(code);
  }

  @Mapping(target = "code", source = "code")
  @Mapping(target = "rates", source = "rates", qualifiedByName = "mapRates")
  public abstract CurrencyModel map(
      @NotNull Currency code, @NotEmpty Map<CurrencyCode, Double> rates);

  @Mapping(target = "code", source = "code")
  @Mapping(target = "rate", source = "rate")
  public abstract ExchangeRate map(@NotNull CurrencyCode code, @NotNull Double rate);

  @Mapping(target = "code", source = "code")
  @Mapping(target = "rate", source = "rate")
  public abstract ExchangeRate map(@NotNull Currency code, @NotNull Double rate);

  public Map<Currency, Double> map(@NotNull CurrencyModel currency) {
    return currency.getRates().stream()
        .collect(toMap(rate -> Currency.getInstance(rate.getCode()), ExchangeRate::getRate));
  }

  @Named("mapRates")
  protected List<ExchangeRate> mapRates(Map<CurrencyCode, Double> rates) {
    return rates.entrySet().stream().map(entry -> map(entry.getKey(), entry.getValue())).toList();
  }
}
