package com.sy.coladay.init

import org.apache.commons.lang3.StringUtils.isAlpha
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.lang.String.format

class CreateDatabaseTask @JvmOverloads constructor(
    dbAdminUrl: String = System.getenv("DB_ADMIN_URL"),
    dbAdminUser: String =
        System.getenv("DB_ADMIN_USER"),
    dbAdminPassword: String =
        System.getenv("DB_ADMIN_PASSWORD"),
    databaseName: String =
        System.getenv("COLADAY_DB_NAME")
) : Task {
    private val dbAdminUrl: String
    private val dbAdminUser: String
    private val dbAdminPassword: String
    private val databaseName: String

    init {
        require(isAlpha(databaseName)) {
            format("Database name %s must contain letters only", databaseName)
        }
        this.dbAdminUrl = dbAdminUrl
        this.dbAdminUser = dbAdminUser
        this.dbAdminPassword = dbAdminPassword
        this.databaseName = databaseName
    }

    override fun exec() {
        LOG.info(
            "Running database creation task with db admin url {}, user {} and cola db name {} ",
            dbAdminUrl, dbAdminUser, databaseName)

        val dataSource = DriverManagerDataSource()
        dataSource.apply {
            setDriverClassName("org.postgresql.Driver")
            url = dbAdminUrl
            username = dbAdminUser
            password = dbAdminPassword
        }

        try {
            // https://www.baeldung.com/sql-injection
            // Could not use prepared statement here as this approach only works for placeholders used
            // as values. The main reason behind this is the very nature of a prepared statement:
            // database server use them to cache the query plan required to pull the result set, which
            // usually is the same for any possible value. This is not true for table names and other
            // constructs available in the SQL language such as columns used in an order by clause.
            with(JdbcTemplate(dataSource)) {
                val dbExists = "SELECT count(*) FROM pg_database WHERE datname = ?"
                queryForObject(dbExists, arrayOf<Any>(databaseName), Int::class.java)
                    .apply {
                        when(this) {
                            0 -> {
                                execute(String.format("CREATE DATABASE %s", databaseName))
                                LOG.info("Database {} created", databaseName)
                            }
                            else -> LOG.info("Skip creating database {} as it already exist", databaseName)
                        }
                    }
            }
        } catch (throwable: Throwable) {
            throw DatabaseCreationFailure(throwable)
        }
    }

    companion object {

        private val LOG: Logger = LoggerFactory.getLogger(CreateDatabaseTask::class.java)
    }

    private inner class DatabaseCreationFailure internal constructor(throwable: Throwable?) :
        RuntimeException(throwable)
}