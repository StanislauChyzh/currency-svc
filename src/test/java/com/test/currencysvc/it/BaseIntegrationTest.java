package com.test.currencysvc.it;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import lombok.SneakyThrows;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@DirtiesContext
@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestPropertySource("classpath:application-test.properties")
public class BaseIntegrationTest {

  @Container
  protected static final PostgreSQLContainer<PostgresContainer> postgreSQLContainer =
      PostgresContainer.getInstance();

  protected static RestClient restClient = RestClient.builder().build();

  @LocalServerPort protected Integer port;
  protected static MockWebServer mockWebServer;

  @DynamicPropertySource
  static void registerDynamicProperties(final DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    registry.add("spring.datasource.driver-class-name", postgreSQLContainer::getDriverClassName);
  }

  @BeforeAll
  @SneakyThrows
  protected static void setup() {
    mockWebServer = new MockWebServer();
    mockWebServer.start(8567);
    postgreSQLContainer.start();
  }

  @AfterAll
  @SneakyThrows
  protected static void tearDown() {
    mockWebServer.shutdown();
    postgreSQLContainer.stop();
  }
}
