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
object DiscoveryService : ThreadedService(false), Service1<Int> {

    override val logger = KotlinLogging.logger {}

    @Volatile
    private var server: DatagramSocket? = null

    override fun start() {
        start(Configuration.Network.discoveryPort)
    }

    override fun start(arg: Int) {
        logger.info { "Start discovery server on port $arg..." }

        try {
            server = DatagramSocket(arg).also {
                it.reuseAddress = true
            }
        } catch (_: BindException) {
            logger.error {
                "Cannot start discovery server cause port $arg is already in use!" + (WhoBlocks.port(arg)?.let {
                    " (by '${it.name}': ${it.id})"
                } ?: "")
            }
            return
        }

        super.start()
    }

    override fun run() {
        try {
            server?.let { server ->
                val packet = DatagramPacket(ByteArray(4), 4)
                server.receive(packet)
                when (packet.data.toInt()) {
                    0 -> {
                        0.toByteArray().let {
                            server.send(DatagramPacket(it, it.size, packet.address, packet.port))
                        }
                    }
                    1 -> {
                        Configuration.Network.robotUdpPort.toByteArray().let {
                            server.send(DatagramPacket(it, it.size, packet.address, packet.port))
                        }
                    }
                    else -> {
                        logger.warn { "Unsupported discovery ${packet.data.toInt()}" }
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