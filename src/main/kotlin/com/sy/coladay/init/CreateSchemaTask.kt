package com.sy.coladay.init

import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource

@Slf4j
class CreateSchemaTask(
    dbUrl: String,
    dbUser: String,
    dbUserPassword: String,
    dbSchema: String
) : Task {
    private val dbUrl: String
    private val dbUser: String
    private val dbUserPassword: String
    private val dbSchema: String

    init {
        require(StringUtils.isAlpha(dbSchema)) {
            String.format(
                "Database schema %s must contain letters "
                        + "only", dbSchema
            )
        }
        this.dbUrl = dbUrl
        this.dbUser = dbUser
        this.dbUserPassword = dbUserPassword
        this.dbSchema = dbSchema
    }

    constructor() : this(
        System.getenv("COLADAY_DB_URL"),
        System.getenv("COLADAY_DB_USER"),
        System.getenv("COLADAY_DB_USER_PASSWORD"),
        System.getenv("COLADAY_DB_SCHEMA")
    ) {
        require(StringUtils.isAlpha(dbSchema)) {
            String.format(
                "Database schema %s must contain letters "
                        + "only", dbSchema
            )
        }
    }

    override fun exec() {
        try {
            val dataSource = DriverManagerDataSource()
            dataSource.setDriverClassName("org.postgresql.Driver")
            dataSource.url = dbUrl
            dataSource.username = dbUser
            dataSource.password = dbUserPassword
            val jdbcTemplate = JdbcTemplate(dataSource)
            val createSchemaIfNotExists = String.format("CREATE SCHEMA IF NOT EXISTS %s", dbSchema)
            jdbcTemplate.execute(createSchemaIfNotExists)
        } catch (throwable: Throwable) {
            throw DatabaseSchemaCreationFailure(throwable)
        }
    }

    private inner class DatabaseSchemaCreationFailure internal constructor(throwable: Throwable?) :
        RuntimeException(throwable)
}