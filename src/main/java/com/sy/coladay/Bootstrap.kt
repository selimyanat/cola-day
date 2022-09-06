package com.sy.coladay

import com.sy.coladay.init.applicationInitializer
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

/**
 * Bootloader of the cola application.
 *
 * @author selim
 */
@SpringBootApplication
open class Bootstrap {}

fun main(args: Array<String>) {
    if ("true".equals(System.getenv("ENABLE_COLADAY_INIT_MODE"), ignoreCase = true)) {
        applicationInitializer()
        return
    }
    if ("true".equals(System.getenv("ENABLE_COLADAY_LOCAL_DEV"), ignoreCase = true)) {
        applicationInitializer()
        SpringApplication.run(Bootstrap::class.java, *args)
        return
    }
    SpringApplication.run(Bootstrap::class.java, *args)
}