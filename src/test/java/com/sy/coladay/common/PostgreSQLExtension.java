package com.sy.coladay.common;

import static org.testcontainers.containers.PostgreSQLContainer.POSTGRESQL_PORT;

import com.sy.coladay.init.CreateDatabaseTask;
import com.sy.coladay.init.CreateSchemaTask;
import com.sy.coladay.init.DatabaseMigrationTask;
import java.util.Map;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;

/**
 * Extension that starts a PostgreSQL instance before all tests in our test class, configures Spring
 * Boot, and stops the PostgreSQL instance after running tests.
 */
public class PostgreSQLExtension implements BeforeAllCallback, AfterAllCallback {

  private PostgreSQLContainer<?> postgres;

  @Override
  public void beforeAll(ExtensionContext extensionContext) throws Exception {

    postgres = new PostgreSQLContainer<>("postgres:11.4-alpine")
        .withUsername("postgres")
        .withPassword("secret")
        .withDatabaseName("postgres")
        .withTmpFs(Map.of("/var/lib/postgresql/data:rw", "rw"));

    postgres.start();

    var dbBaseUrl =
        "jdbc:postgresql://" +
            postgres.getContainerIpAddress() +
            ":" +
            postgres.getMappedPort(POSTGRESQL_PORT);
    var createDatabaseTask = new CreateDatabaseTask(
        dbBaseUrl + "/postgres",
        postgres.getUsername(),
        postgres.getPassword(),
        "coladay"
    );
    createDatabaseTask.exec();
    ;
    var createSchemaTask = new CreateSchemaTask(
        dbBaseUrl + "/coladay",
        postgres.getUsername(),
        postgres.getPassword(),
        "coladay"
    );
    createSchemaTask.exec();
    var dbMigrationTask = new DatabaseMigrationTask(
        dbBaseUrl + "/coladay?currentSchema=coladay",
        postgres.getUsername(),
        postgres.getPassword()
    );
    dbMigrationTask.exec();

    System.setProperty("spring.datasource.username", postgres.getUsername());
    System.setProperty("spring.datasource.password", postgres.getPassword());
    System.setProperty("spring.datasource.url", dbBaseUrl + "/coladay?currentSchema=coladay");
  }

  @Override
  public void afterAll(ExtensionContext extensionContext) throws Exception {
    postgres.stop();
  }

}
