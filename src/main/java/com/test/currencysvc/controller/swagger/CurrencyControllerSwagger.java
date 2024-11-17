package com.test.currencysvc.controller.swagger;

import com.test.currencysvc.controller.dto.response.CreateResponse;
import com.test.currencysvc.controller.dto.response.CurrencyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Currency", description = "APIs to manage currencies.")
public interface CurrencyControllerSwagger {

  @Operation(summary = "Find all available currencies.")
  ResponseEntity<List<CurrencyResponse>> findAllAvailable();

  @Operation(summary = "Find exchange rates by currency code.")
  ResponseEntity<Map<Currency, Double>> findRatesByCode(
      @Parameter(description = "Currency code") @PathVariable Currency code);

  @Operation(summary = "Subscribe on currency exchange rate.")
  ResponseEntity<CreateResponse<Long>> subscribe(
      @Parameter(description = "Currency code") @PathVariable Currency code);
}
