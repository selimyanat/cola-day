package com.sy.coladay

import org.springframework.boot.autoconfigure.SpringBootApplication
import kotlin.jvm.JvmStatic
import com.sy.coladay.init.ApplicationInitializer
import org.springframework.boot.SpringApplication

/**
 * Bootloader of the cola application.
 *
 * @author selim
 */
@SpringBootApplication
open class Bootstrap {
}

fun main(args: Array<String>) {
    if ("true".equals(System.getenv("ENABLE_COLADAY_INIT_MODE"), ignoreCase = true)) {
        val initializer = ApplicationInitializer()
        initializer.initApplication()
        return
    }
    if ("true".equals(System.getenv("ENABLE_COLADAY_LOCAL_DEV"), ignoreCase = true)) {
        val initializer = ApplicationInitializer()
        initializer.initApplication()
        SpringApplication.run(Bootstrap::class.java, *args)
        return
    }
    SpringApplication.run(Bootstrap::class.java, *args)
}