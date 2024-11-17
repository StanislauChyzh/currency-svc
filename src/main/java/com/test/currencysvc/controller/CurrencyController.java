package com.test.currencysvc.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.test.currencysvc.controller.dto.response.CreateResponse;
import com.test.currencysvc.controller.dto.response.CurrencyResponse;
import com.test.currencysvc.controller.swagger.CurrencyControllerSwagger;
import com.test.currencysvc.service.CurrencyExchangeRateService;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/v1/currencies")
public class CurrencyController implements CurrencyControllerSwagger {

  private final CurrencyExchangeRateService service;

  @Override
  @GetMapping(produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CurrencyResponse>> findAllAvailable() {
    log.info("Finding all available currencies.");
    return ResponseEntity.ok(
        service.findAvailableCodes().stream()
            .map(currency -> new CurrencyResponse(currency, currency.getDisplayName()))
            .toList());
  }

  @Override
  @GetMapping(value = "/{code}/rates", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<Map<Currency, Double>> findRatesByCode(@PathVariable Currency code) {
    log.info("Finding exchange rates for {}.", code);
    final var rates = service.loadRatesByCode(code);
    return ResponseEntity.ok(rates);
  }

  @Override
  @PostMapping(value = "/{code}/subscribe", produces = APPLICATION_JSON_VALUE)
  public ResponseEntity<CreateResponse<Long>> subscribe(@PathVariable Currency code) {
    log.info("Subscribing on {} currency exchange rates.", code);
    final var id = service.subscribe(code);
    return ResponseEntity.ok(new CreateResponse<>(id));
  }
}
