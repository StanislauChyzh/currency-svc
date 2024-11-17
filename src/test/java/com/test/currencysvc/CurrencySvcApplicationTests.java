package com.test.currencysvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

@ExtendWith(MockitoExtension.class)
class CurrencySvcApplicationTests {

  @Test
  void shouldReturnNothing_whenMain_givenArgs() {
    try (var springApplication = mockStatic(SpringApplication.class)) {
      // arrange
      final String[] args = {""};

      final var context = mock(ConfigurableApplicationContext.class);

      springApplication
          .when(() -> SpringApplication.run(CurrencySvcApplication.class, args))
          .thenReturn(context);

      // act
      CurrencySvcApplication.main(args);

      // assert
      springApplication.verify(() -> SpringApplication.run(CurrencySvcApplication.class, args));
    }
  }
}
