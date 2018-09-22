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
object WebService : Service1<Int> {
    override val running: Boolean
        get() = server != null
    var server: Javalin? = null

    override fun start() {
        start(Configuration.Network.webPort)
    }

    private val logger = KotlinLogging.logger {}

    private val resourceHandler =
            ResourceHandler("website", Configuration.tmp("website"))

    private val connection = WebSocketConnection()

    override fun start(arg: Int) {
        if (running) return
        logger.info { "Start web server on port $arg... (http://localhost:$arg)" }

        updateColors()

        server = Javalin.create().apply {
            port(arg)
            event(EventType.SERVER_START_FAILED) {
                logger.error {
                    "Cannot start discovery server, cause port $arg is already in use!" + (WhoBlocks.port(arg)?.let {
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