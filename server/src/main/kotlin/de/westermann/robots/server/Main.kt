package de.westermann.robots.server

import de.westermann.robots.server.service.DiscoveryService
import de.westermann.robots.server.service.ReplService
import de.westermann.robots.server.service.WebService
import de.westermann.robots.server.utils.Configuration
import mu.KotlinLogging
import kotlin.concurrent.thread
import kotlin.system.exitProcess

object Main {
    private val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
        if ("--help" in args) {
            Configuration.help {
                println(it)
            }
            exitProcess(0)
        }

        Configuration.load(args.toList())

        Configuration.tmpClear()
        logger.info { "Temp folder is ${Configuration.properties.tmpDirectory.toAbsolutePath()}" }

        if (Configuration.properties.demo) {
            logger.info { "Enter demo modus" }
            Demo.load()
        }

        DiscoveryService.start(Configuration.properties.discoveryPort)
        WebService.start(Configuration.properties.webPort)
        ReplService.start()

        Runtime.getRuntime().addShutdownHook(thread(start = false, name = "shutdown") {
            logger.info { "Stopping..." }

            Configuration.tmpClear()
            DiscoveryService.stop()
            ReplService.stop()
        })
    }

    fun stop() {
        exitProcess(0)
    }
}

