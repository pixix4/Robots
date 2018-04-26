package de.westermann.robots.server.service

import de.westermann.robots.robot.Discovery
import de.westermann.robots.server.utils.WhoBlocks
import java.net.BindException
import java.net.DatagramSocket
import java.net.SocketException

/**
 * @author lars
 */
object DiscoveryService: Service(false) {

    @Volatile
    private var server: DatagramSocket? = null

    override fun start() {
        throw NotImplementedError()
    }

    fun start(port: Int): Boolean {
        logger.info { "Start discovery server on port $port..." }

        try {
            server = DatagramSocket(port)
        } catch (_: BindException) {
            logger.error { "Cannot start discovery server cause port $port is already in use!" + (WhoBlocks.port(port)?.let {
                " (by '${it.name}': ${it.id})"
            } ?: "") }
            return false
        }

        super.start()

        return true
    }

    override fun run() {
        try {
            server?.let { server ->
                Discovery.receive(server).let { packet ->
                    when (packet) {
                        is Discovery.Inquire -> Discovery.send(
                                Discovery.Response(0),
                                server,
                                packet.address!!
                        )
                        else -> TODO()
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