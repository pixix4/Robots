package de.westermann.robots.lego

import de.westermann.robots.robot.toByteArray
import de.westermann.robots.robot.toDataInt
import java.net.*
import kotlin.concurrent.thread

/**
 * @author lars
 */
object DiscoveryClient {
    fun find(port: Int, onFound: (address: InetAddress, port: Int) -> Unit) {
        val broadcastAddresses = NetworkInterface.getNetworkInterfaces().toList().flatMap {
            it.interfaceAddresses.map { it.broadcast }
        }.filterNotNull()

        thread(name = "discovery") {
            var running = true
            val socket = DatagramSocket()
            socket.soTimeout = 1000

            while (running) {
                for (addr in broadcastAddresses) {
                    try {
                        val sendPacket = 0.toByteArray()
                        socket.send(DatagramPacket(sendPacket, sendPacket.size, addr, port))

                        val packet = DatagramPacket(ByteArray(4), 4)
                        socket.receive(packet)
                        val p = packet.data.toDataInt()
                        if (p != 0) {
                            onFound(packet.address, p)
                            running = false
                            break
                        }
                    } catch (_: SocketTimeoutException) {
                        //Useless
                    }
                }
            }
        }
    }
}