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
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseMigrationTask implements Task {

  private String dbUrl;

  private String dbUser;

  private String dbUserPassword;

  public DatabaseMigrationTask(@NonNull String dbUrl,
                               @NonNull String dbUser,
                               @NonNull String dbUserPassword) {

    this.dbUrl = dbUrl;
    this.dbUser = dbUser;
    this.dbUserPassword = dbUserPassword;
  }

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
