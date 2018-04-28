package de.westermann.robots.server.service

import de.westermann.robots.server.utils.WhoBlocks
import io.javalin.Javalin
import io.javalin.event.EventType
import mu.KotlinLogging

/**
 * @author lars
 */
object WebService {
    private val logger = KotlinLogging.logger {}

    fun start(port: Int) {
        logger.info { "Start web server on port $port... (http://localhost:$port)" }

        Javalin.create().apply {
            port(port)

            event(EventType.SERVER_START_FAILED) {
                logger.error {
                    "Cannot start discovery server cause port $port is already in use!" + (WhoBlocks.port(port)?.let {
                        " (by '${it.name}': ${it.id})"
                    } ?: "")
                }
            }

            enableDynamicGzip()

            exception(Exception::class.java) { exception, ctx ->
                logger.warn("Exception in web server", exception)
            }

            enableStaticFiles("website")

            ws("/ws") {
                it.onConnect { session ->

                }
                it.onMessage { session, msg ->

                }
                it.onClose { session, _, _ ->

                }
            }
        }.start()
    }

    fun stop() {

    }
}