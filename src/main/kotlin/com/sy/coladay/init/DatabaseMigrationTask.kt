package com.sy.coladay.init

import liquibase.Contexts
import liquibase.LabelExpression
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.LiquibaseException
import liquibase.resource.ClassLoaderResourceAccessor
import lombok.extern.slf4j.Slf4j
import java.sql.DriverManager
import java.sql.SQLException

@Slf4j
class DatabaseMigrationTask @JvmOverloads constructor(
    private val dbUrl: String = System.getenv("COLADAY_MIGRATION_DB_URL"),
    private val dbUser: String = System.getenv("COLADAY_MIGRATION_DB_USER"),
    private val dbUserPassword: String = System.getenv("COLADAY_MIGRATION_DB_USER_PASSWORD"))
    : Task {

    override fun exec() {
        try {
            DriverManager.getConnection(dbUrl, dbUser, dbUserPassword).use { connection ->
                val jdbcConnection = JdbcConnection(connection)
                val database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(jdbcConnection)
                val liquibase = Liquibase(
                    "db/changelog/changelog-master.xml",
                    ClassLoaderResourceAccessor(),
                    database
                )
                liquibase.update(Contexts(), LabelExpression())
            }
        } catch (exception: SQLException) {
            // should return a result instead of void!
            throw DatabaseMigrationFailure(exception)
        } catch (exception: LiquibaseException) {
            throw DatabaseMigrationFailure(exception)
        }
    }

    private inner class DatabaseMigrationFailure internal constructor(throwable: Throwable?) :
        RuntimeException(throwable)
}