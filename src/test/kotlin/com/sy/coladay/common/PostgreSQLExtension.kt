package com.sy.coladay.common

import com.sy.coladay.init.CreateDatabaseTask
import com.sy.coladay.init.CreateSchemaTask
import com.sy.coladay.init.DatabaseMigrationTask
import org.junit.jupiter.api.extension.AfterAllCallback
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import org.testcontainers.containers.PostgreSQLContainer
import java.util.Map

/**
 * Extension that starts a PostgreSQL instance before all tests in our test class, configures Spring
 * Boot, and stops the PostgreSQL instance after running tests.
 */
class PostgreSQLExtension : BeforeAllCallback, AfterAllCallback {

    private var postgres: PostgreSQLContainer<*>? = null

    @Throws(Exception::class)
    override fun beforeAll(extensionContext: ExtensionContext) {
        postgres = PostgreSQLContainer("postgres:11.4-alpine")
            .withUsername("postgres")
            .withPassword("secret")
            .withDatabaseName("postgres")
            .withTmpFs(Map.of("/var/lib/postgresql/data:rw", "rw"))
        postgres!!.start()
        val dbBaseUrl = ("jdbc:postgresql://"
                + postgres!!.containerIpAddress
                + ":"
                + postgres!!.getMappedPort(PostgreSQLContainer.POSTGRESQL_PORT))
        val createDatabaseTask = CreateDatabaseTask(
            "$dbBaseUrl/postgres",
            postgres!!.username,
            postgres!!.password,
            "coladay"
        )
        createDatabaseTask.exec()
        val createSchemaTask = CreateSchemaTask(
            "$dbBaseUrl/coladay",
            postgres!!.username,
            postgres!!.password,
            "coladay"
        )
        createSchemaTask.exec()
        val dbMigrationTask = DatabaseMigrationTask(
            "$dbBaseUrl/coladay?currentSchema=coladay",
            postgres!!.username,
            postgres!!.password
        )
        dbMigrationTask.exec()
        System.setProperty("spring.datasource.username", postgres!!.username)
        System.setProperty("spring.datasource.password", postgres!!.password)
        System.setProperty("spring.datasource.url", "$dbBaseUrl/coladay?currentSchema=coladay")
    }

    @Throws(Exception::class)
    override fun afterAll(extensionContext: ExtensionContext) {
        postgres!!.stop()
    }
}