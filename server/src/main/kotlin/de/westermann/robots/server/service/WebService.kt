package de.westermann.robots.server.service

import de.westermann.robots.datamodel.util.Json
import de.westermann.robots.server.connection.WebSocketConnection
import de.westermann.robots.server.util.Configuration
import de.westermann.robots.server.util.ResourceHandler
import de.westermann.robots.server.util.WhoBlocks
import io.javalin.Javalin
import io.javalin.event.EventType
import mu.KotlinLogging
import java.nio.file.Files

/**
 * @author lars
 */
object WebService : Service {
    override val running: Boolean
        get() = server != null
    var server: Javalin? = null

    override fun start() {
        start(Configuration.properties.webPort)
    }

    private val logger = KotlinLogging.logger {}

    private val resourceHandler =
            ResourceHandler("website", Configuration.tmp("website"))

    private val connection = WebSocketConnection()

    fun start(port: Int) {
        if (running) return
        logger.info { "Start web server on port $port... (http://localhost:$port)" }

        updateColors()

        server = Javalin.create().apply {
            port(port)
            event(EventType.SERVER_START_FAILED) {
                logger.error {
                    "Cannot start discovery server, cause port $port is already in use!" + (WhoBlocks.port(port)?.let {
                        " (by '${it.name}': ${it.id})"
                    } ?: "")
                }
            }

            enableDynamicGzip()

            exception(Exception::class.java) { exception, _ ->
                logger.warn("Exception in web server", exception)
            }

            resourceHandler.walkFiles { path, file ->
                get("public/${path.replace("\\", "/")}") {
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
                    connection += session
                }
                it.onMessage { session, msg ->
                    if (msg == "ping") {
                        session.send("pong")
                        return@onMessage
                    }
                    connection.execute(session, Json.fromString(msg))
                }
                it.onClose { session, _, _ ->
                    connection -= session
                }
            }
        }.start()
    }

    fun updateColors() {
        resourceHandler.compileSass()
    }

    override fun stop() {
        server?.stop()
        server = null
    }
}