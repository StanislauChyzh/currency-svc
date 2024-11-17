package com.test.currencysvc.job;

import static com.test.currencysvc.client.dto.CurrencyCode.USD;
import static org.mockito.Mockito.*;

import com.test.currencysvc.client.CurrencyExchangeRateClient;
import com.test.currencysvc.service.CurrencyExchangeRateService;
import com.test.currencysvc.service.dto.UpdateRate;
import com.test.currencysvc.service.mapper.CurrencyExchangeRateMapperImpl;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {SyncExchangeRateJob.class, CurrencyExchangeRateMapperImpl.class})
class SyncExchangeRateJobTest {

  @Autowired private SyncExchangeRateJob syncExchangeRateJob;

  @MockBean private CurrencyExchangeRateService currencyExchangeRateService;

  @MockBean private CurrencyExchangeRateClient freeCurrencyClient;

  @Test
  void shouldReturnNothing_whenSync_givenNothing() {
    // arrange
    final var huf = Currency.getInstance("USD");
    final var rate = new UpdateRate(huf, Map.of(huf, 2.0));

    when(currencyExchangeRateService.findExistingCodes())
        .thenReturn(List.of(Currency.getInstance("USD")));
    when(freeCurrencyClient.findLatestRates(USD)).thenReturn(Map.of(USD, 2.0));
    doNothing().when(currencyExchangeRateService).update(List.of(rate));

    // act
    syncExchangeRateJob.sync();

    // assert
    verify(currencyExchangeRateService).update(List.of(rate));
  }
}
