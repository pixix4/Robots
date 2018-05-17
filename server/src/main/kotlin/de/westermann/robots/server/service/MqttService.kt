package de.westermann.robots.server.service

import de.westermann.robots.server.util.Configuration
import io.moquette.interception.AbstractInterceptHandler
import io.moquette.interception.InterceptHandler
import io.moquette.interception.messages.*
import io.moquette.server.Server
import mu.KotlinLogging
import java.util.*


/**
 * @author lars
 */
object MqttService : Service {

    val logger = KotlinLogging.logger {}
    override val running: Boolean
        get() = server != null
    var server: Server? = null

    override fun start() {
        start(Configuration.properties.robotPort)
    }

    fun start(port: Int) {
        if (running) return
        server = Server()
        server?.let {
            logger.info { "Start mqtt server on port $port..." }

            val properties = Properties()
            properties.setProperty("port", port.toString())
            properties.setProperty("host", "0.0.0.0")
            properties.setProperty("allow_anonymous", "true")
            it.startServer(properties)
            it.addInterceptHandler(object : AbstractInterceptHandler() {
                override fun getID(): String = "Robot Mqtt Server"

                override fun onPublish(msg: InterceptPublishMessage?) {
                    TODO()
                }

                override fun onSubscribe(msg: InterceptSubscribeMessage?) {
                    TODO()
                }

                override fun onUnsubscribe(msg: InterceptUnsubscribeMessage?) {
                    TODO()
                }
            })
        }
    }

    override fun stop() {
        server?.stopServer()
        server = null
    }
}