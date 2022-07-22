package com.sy.coladay.init

class ApplicationInitializer {

    fun initApplication() {
        createDatabaseIfNotExists()
        createSchemaIfNotExists()
        runDatabaseMigration()
    }

    private fun createDatabaseIfNotExists() {
        val task = CreateDatabaseTask()
        task.exec()
    }

    private fun createSchemaIfNotExists() {
        val task = CreateSchemaTask()
        task.exec()
    }

    private fun runDatabaseMigration() {
        val task = DatabaseMigrationTask()
        task.exec()
    }
}