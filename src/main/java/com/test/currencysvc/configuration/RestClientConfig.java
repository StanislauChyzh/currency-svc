package com.test.currencysvc.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
public class RestClientConfig {

  @Bean(name = "externalRestClient")
  public RestClient externalRestClient() {
    return RestClient.builder().build();
  }
}
