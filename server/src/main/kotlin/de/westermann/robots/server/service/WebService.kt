package de.westermann.robots.server.service

import io.javalin.Javalin
import mu.KotlinLogging

/**
 * @author lars
 */
object WebService {
    private val logger = KotlinLogging.logger {}

    fun start(port: Int) {
        Javalin.create().apply {
            port(port)

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

        logger.info { "Start web server on port $port... (http://localhost:$port)" }
    }

    fun stop() {

    }
}