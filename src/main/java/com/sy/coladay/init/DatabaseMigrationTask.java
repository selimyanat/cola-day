package com.sy.coladay.init;

import static java.lang.System.getenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DatabaseMigrationTask implements Task {

  @NonNull
  private String dbUrl;
  @NonNull
  private String dbUser;
  @NonNull
  private String dbUserPassword;

  public DatabaseMigrationTask() {

    this(getenv("COLADAY_MIGRATION_DB_URL"),
         getenv("COLADAY_MIGRATION_DB_USER"),
         getenv("COLADAY_MIGRATION_DB_USER_PASSWORD")
    );
  }

  public void exec() {

    try (final Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbUserPassword)) {
      var jdbcConnection = new JdbcConnection(connection);
      var database = DatabaseFactory.getInstance()
                                    .findCorrectDatabaseImplementation(jdbcConnection);
      var liquibase = new liquibase.Liquibase("db/changelog/changelog-master.xml",
                                              new ClassLoaderResourceAccessor(),
                                              database
      );
      liquibase.update(new Contexts(), new LabelExpression());
    } catch (SQLException | LiquibaseException exception) {
      // should return a result instead of void!
      throw new DatabaseMigrationFailure(exception);
    }
  }

  private class DatabaseMigrationFailure extends RuntimeException {

    DatabaseMigrationFailure(Throwable throwable) {
      super(throwable);
    }
  }
}
