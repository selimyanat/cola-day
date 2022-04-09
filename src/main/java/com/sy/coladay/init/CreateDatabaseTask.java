package com.sy.coladay.init;

import static java.lang.String.format;
import static java.lang.System.getenv;
import static org.apache.commons.lang3.StringUtils.isAlpha;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Slf4j
public final class CreateDatabaseTask implements Task {


  private final String dbAdminUrl;

  private final String dbAdminUser;

  private final String dbAdminPassword;

  private final String databaseName;


  public CreateDatabaseTask(@NonNull String dbAdminUrl,
                            @NonNull String dbAdminUser,
                            @NonNull String dbAdminPassword,
                            @NonNull String databaseName) {

    if (!isAlpha(databaseName)) {
      throw new IllegalArgumentException(format("Database name %s must contain letters "
                                                    + "only", databaseName));
    }

    this.dbAdminUrl = dbAdminUrl;
    this.dbAdminUser = dbAdminUser;
    this.dbAdminPassword = dbAdminPassword;
    this.databaseName = databaseName;
  }

  public CreateDatabaseTask() {

    this(getenv("DB_ADMIN_URL"),
         getenv("DB_ADMIN_USER"),
         getenv("DB_ADMIN_PASSWORD"),
         getenv("COLADAY_DB_NAME")
    );
  }

  public void exec() {

    LOG.info("Running database creation task with db admin url {}, user {} and cola db name {} ",
             this.dbAdminUrl, this.dbAdminUser, this.databaseName
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

      // https://www.baeldung.com/sql-injection
      // Could not use prepared statement here as this approach only works for placeholders used
      // as values. The main reason behind this is the very nature of a prepared statement:
      // database server use them to cache the query plan required to pull the result set, which
      // usually is the same for any possible value. This is not true for table names and other
      // constructs available in the SQL language such as columns used in an order by clause.
      if (count == 0) {
        jdbcTemplate.execute(format("CREATE DATABASE %s", databaseName));
        LOG.info("Database {} created", databaseName);
      } else {
        LOG.info("Skip creating database {} as it already exist", databaseName);
      }
    } catch (Throwable throwable) {
      throw new DatabaseCreationFailure(throwable);
    }
  }

  private class DatabaseCreationFailure extends RuntimeException {

    DatabaseCreationFailure(Throwable throwable) {
      super(throwable);
    }
  }
}