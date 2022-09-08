package com.sy.coladay.init

fun applicationInitializer() {
    CreateDatabaseTask().exec()
    CreateSchemaTask().exec()
    DatabaseMigrationTask().exec()
}