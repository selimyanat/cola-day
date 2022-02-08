package com.sy.coladay.init;

import static java.lang.System.getenv;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@RequiredArgsConstructor
@Slf4j
public final class CreateDatabaseTask implements Task {

  @NonNull
  private final String dbAdminUrl;
  @NonNull
  private final String dbAdminUser;
  @NonNull
  private final String dbAdminPassword;
  @NonNull
  private final String databaseName;

  public CreateDatabaseTask() {

    // TODO throw exceptions if not set
    this(getenv("DB_ADMIN_URL"),
         getenv("DB_ADMIN_USER"),
         getenv("DB_ADMIN_PASSWORD"),
         getenv("COLADAY_DB_NAME")
    );
  }

  public void exec() {

    LOG.info("Running database creation task with db admin url {}, user {} and cola db name {} ",
             this.dbAdminUrl, this.dbAdminUser, this.dbAdminPassword
    );
    try {
      var dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("org.postgresql.Driver");
      dataSource.setUrl(dbAdminUrl);
      dataSource.setUsername(dbAdminUser);
      dataSource.setPassword(dbAdminPassword);
      var jdbcTemplate = new JdbcTemplate(dataSource);
      var dbExists = "SELECT count(*) FROM pg_database WHERE datname = ?";
      var count = jdbcTemplate.queryForObject(dbExists,
                                              new Object[]{databaseName},
                                              Integer.class
      );
      if (count == 0) {
        jdbcTemplate.execute("CREATE DATABASE " + databaseName);
        LOG.info("Database {} created", databaseName);
      } else {
        LOG.info("Skip creating database {} as it already exist", databaseName);
      }
    } catch (Throwable throwable) {
      throw new RuntimeException(throwable);
    }
  }
}