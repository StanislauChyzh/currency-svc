package com.test.currencysvc.service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Currency;
import java.util.Map;

public record UpdateRate(@NotNull Currency currency, @NotEmpty Map<Currency, Double> rates) {}
