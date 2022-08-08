package com.sy.coladay.init

import java.util.stream.Stream

class ApplicationInitializer {

    fun initApplication() {

        Stream.of(CreateDatabaseTask(), CreateSchemaTask(), DatabaseMigrationTask())
            .forEach(Task::exec)
    }
}