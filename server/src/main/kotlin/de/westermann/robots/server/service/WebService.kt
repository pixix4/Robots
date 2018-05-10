package de.westermann.robots.server.service

import de.westermann.robots.server.utils.Configuration
import de.westermann.robots.server.utils.ResourceHandler
import de.westermann.robots.server.utils.WhoBlocks
import io.javalin.Javalin
import io.javalin.event.EventType
import mu.KotlinLogging
import java.nio.file.Files

/**
 * @author lars
 */
object WebService {
    private val logger = KotlinLogging.logger {}

    private val resourceHandler =
            ResourceHandler("website", Configuration.tmp("website"))

    fun start(port: Int) {
        logger.info { "Start web server on port $port... (http://localhost:$port)" }

        updateColors()

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

            exception(Exception::class.java) { exception, _ ->
                logger.warn("Exception in web server", exception)
            }

            resourceHandler.walkFiles { path, file ->
                get("public/$path") {
                    it.header("Content-Type", ResourceHandler.getMimeType(file))
                    it.result(Files.newInputStream(file))
                }
            }
            resourceHandler.find("index.html")?.also { file ->
                get("/*") {
                    it.header("Content-Type", ResourceHandler.getMimeType(file))
                    it.result(Files.newInputStream(file))
                }
            }

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

    fun updateColors() {
        resourceHandler.compileSass()
    }

    fun stop() {

    }
}