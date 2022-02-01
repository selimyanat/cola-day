package com.sy.coladay.init;

public class ApplicationInitializer {

  public final void initApplication() {

    createDatabaseIfNotExists();
    createSchemaIfNotExists();
    runDatabaseMigration();
  }

  private void createDatabaseIfNotExists() {

    var task = new CreateDatabaseTask();
    task.exec();;
  }

    private void createSchemaIfNotExists() {

      var task = new CreateSchemaTask();
      task.exec();
  }

  private void runDatabaseMigration() {

    var task = new DatabaseMigrationTask();
    task.exec();
  }
}
