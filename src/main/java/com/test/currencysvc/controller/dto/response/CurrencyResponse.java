package com.test.currencysvc.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Currency response")
public class CurrencyResponse {

  @Schema(name = "code", description = "currency code", example = "EUR")
  private Currency code;

  @Schema(name = "name", description = "currency name", example = "Euro")
  private String name;
}
