package com.sy.coladay.init;

import static java.lang.System.getenv;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@RequiredArgsConstructor
@Slf4j
public class CreateSchemaTask implements Task {

  @NonNull
  private final String dbUrl;
  @NonNull
  private final String dbUser;
  @NonNull
  private final String dbUserPassword;
  @NonNull
  private final String dbSchema;

  public CreateSchemaTask() {

    // TODO throw exceptions if not set
    this(getenv("COLADAY_DB_URL"),
         getenv("COLADAY_DB_USER"),
         getenv("COLADAY_DB_USER_PASSWORD"),
         getenv("COLADAY_DB_SCHEMA")
    );
  }

  public void exec() {

    try {
      var dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("org.postgresql.Driver");
      dataSource.setUrl(dbUrl);
      dataSource.setUsername(dbUser);
      dataSource.setPassword(dbUserPassword);
      var jdbcTemplate = new JdbcTemplate(dataSource);
      var createSchemaIfNotExists = "CREATE SCHEMA IF NOT EXISTS " + dbSchema;
      jdbcTemplate.execute(createSchemaIfNotExists);
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }
}
