package de.westermann.robots.server

import de.westermann.robots.server.service.DiscoveryService
import de.westermann.robots.server.service.ReplService
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

        DiscoveryService.start(Configuration.properties.discoveryPort)
        ReplService.start()

        Runtime.getRuntime().addShutdownHook(thread(start = false, name = "shutdown") {
            logger.info { "Stopping..." }

            DiscoveryService.stop()
            ReplService.stop()

            logger.info { "Exit" }
        })
    }

    fun stop() {
        exitProcess(0)
    }
}

