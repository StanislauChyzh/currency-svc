package com.test.currencysvc.controller.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Create response")
public class CreateResponse<T> {

  @Schema(name = "id", description = "created id", examples = "1")
  private T id;
}
