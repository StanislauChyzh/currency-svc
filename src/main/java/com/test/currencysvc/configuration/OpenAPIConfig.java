package com.test.currencysvc.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenAPIConfig {

  @Bean
  public OpenAPI documentation() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Currency service")
                .version("1.0")
                .description("This service exposes endpoints to manage currency."));
  }
}
