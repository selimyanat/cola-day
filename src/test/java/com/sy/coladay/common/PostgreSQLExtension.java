package com.sy.coladay.common;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Extension that starts a PostgreSQL instance before all tests in our test class, configures
 * Spring Boot, and stops the PostgreSQL instance after running tests.
 */
public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {

  private PostgreSQLContainer<?> postgres;

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {
    postgres = new PostgreSQLContainer<>("postgres:11.4-alpine")
        .withDatabaseName("testdb")
        .withUsername("postgres")
        .withPassword("secret");
//          .withTmpFs(Map.of("/var/lib/postgresql/data:rw", "rw"));
//  https://proitcsolution.com.ve/how-to-use-testcontainers-with-integration-tests/
//          .withTmpFs(Map.of("/var/lib/postgresql/data:rw", "rw"));
    postgres.start();
    System.setProperty("spring.datasource.url", postgres.getJdbcUrl());
    System.setProperty("spring.datasource.password", postgres.getPassword());
    System.setProperty("spring.datasource.username", postgres.getUsername());
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {

  }

}
