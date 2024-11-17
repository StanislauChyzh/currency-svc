package com.test.currencysvc;

import com.test.currencysvc.client.dto.FreeCurrencyProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties({FreeCurrencyProperties.class})
public class CurrencySvcApplication {

  public static void main(String[] args) {
    SpringApplication.run(CurrencySvcApplication.class, args);
  }
}
