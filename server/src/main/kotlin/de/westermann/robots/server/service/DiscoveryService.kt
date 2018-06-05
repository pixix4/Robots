package de.westermann.robots.server.service

import de.westermann.robots.server.connection.toByteArray
import de.westermann.robots.server.connection.toInt
import de.westermann.robots.server.util.Configuration
import de.westermann.robots.server.util.WhoBlocks
import mu.KotlinLogging
import java.net.BindException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.SocketException
import kotlin.jvm.Volatile

/**
 * @author lars
 */
object DiscoveryService : ThreadedService(false) {

    override val logger = KotlinLogging.logger {}

    @Volatile
    private var server: DatagramSocket? = null

    override fun start() {
        start(Configuration.properties.discoveryPort)
    }

    fun start(port: Int): Boolean {
        logger.info { "Start discovery server on port $port..." }

        try {
            server = DatagramSocket(port).also {
                it.reuseAddress = true
            }
        } catch (_: BindException) {
            logger.error {
                "Cannot start discovery server cause port $port is already in use!" + (WhoBlocks.port(port)?.let {
                    " (by '${it.name}': ${it.id})"
                } ?: "")
            }
            return false
        }

        super.start()

        return true
    }

    override fun run() {
        try {
            server?.let { server ->
                val packet = DatagramPacket(ByteArray(4), 4)
                server.receive(packet)
                if (packet.data.toInt() == 0) {
                    Configuration.properties.robotPort.toByteArray().let {
                        server.send(DatagramPacket(it, it.size, packet.address, packet.port))
                    }
                }
            }
        } catch (e: SocketException) {
            if (server != null) {
                logger.error { "Socket error in discovery server" }
            }
        }
    }

    override fun stop() {
        server?.let {
            server = null
            it.close()
        }
        super.stop()
    }
}