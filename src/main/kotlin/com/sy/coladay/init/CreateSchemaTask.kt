package com.sy.coladay.init

import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils.isAlpha
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.datasource.DriverManagerDataSource
import java.lang.String.format

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
        require(isAlpha(dbSchema)) {
            format("Database schema %s must contain letters only", dbSchema)
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
        require(isAlpha(dbSchema)) {
            format("Database schema %s must contain letters only", dbSchema)
        }
    }

    override fun exec() {

        val dataSource = DriverManagerDataSource()
        dataSource.apply {
            setDriverClassName("org.postgresql.Driver")
            url = dbUrl
            username = dbUser
            password = dbUserPassword
        }

        try {
            with(JdbcTemplate(dataSource)) {
                execute("CREATE SCHEMA IF NOT EXISTS $dbSchema")
            }
        } catch (throwable: Throwable) {
            throw DatabaseSchemaCreationFailure(throwable)
        }
    }

    private inner class DatabaseSchemaCreationFailure internal constructor(throwable: Throwable?) :
        RuntimeException(throwable)
}