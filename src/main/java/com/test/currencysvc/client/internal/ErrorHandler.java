package com.test.currencysvc.client.internal;

import static java.text.MessageFormat.format;

import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

@RequiredArgsConstructor
class ErrorHandler implements ResponseErrorHandler {

  private final String message;

  @Override
  public boolean hasError(ClientHttpResponse response) throws IOException {
    return response.getStatusCode().is5xxServerError()
        || response.getStatusCode().is4xxClientError();
  }

  @Override
  public void handleError(@NotNull ClientHttpResponse response) throws IOException {
    throw new RuntimeException(
        format(
            "{0}, status: {1}, message: {2}",
            message, response.getStatusCode(), response.getStatusText()));
  }
}
