package com.test.currencysvc.it;

import org.testcontainers.containers.PostgreSQLContainer;

public final class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

  private static final String IMAGE_VERSION = "postgres:latest";
  private static PostgresContainer container;

  private PostgresContainer() {
    super(IMAGE_VERSION);
  }

  public static PostgresContainer getInstance() {
    if (container == null) {
      container = new PostgresContainer();
    }
    return container;
  }

  @Override
  public void start() {
    super.start();
  }
}
