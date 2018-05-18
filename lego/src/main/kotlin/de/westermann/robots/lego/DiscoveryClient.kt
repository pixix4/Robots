package de.westermann.robots.lego

import de.westermann.robots.robot.toByteArray
import de.westermann.robots.robot.toInt
import java.net.*
import kotlin.concurrent.thread
import kotlin.jvm.Volatile

/**
 * @author lars
 */
object DiscoveryClient {

    @Volatile
    private var running: Boolean = false

    fun find(port: Int, onFound: (address: InetAddress, port: Int) -> Boolean) {
        val broadcastAddresses = NetworkInterface.getNetworkInterfaces().toList().flatMap {
            it.interfaceAddresses.map { it.broadcast }
        }.filterNotNull()

        println("Try to find server on port $port")
        println("Search in networks "+broadcastAddresses.map { it.toString().replace("/","") })

        thread(name = "discovery") {
            running = true
            val socket = DatagramSocket()
            socket.soTimeout = 1000
            socket.reuseAddress = true

            while (running) {
                for (addr in broadcastAddresses) {
                    try {
                        val sendPacket = 0.toByteArray()
                        socket.send(DatagramPacket(sendPacket, sendPacket.size, addr, port))

                        val packet = DatagramPacket(ByteArray(4), 4)
                        socket.receive(packet)
                        val p = packet.data.toInt()
                        if (p != 0 && onFound(packet.address, p)) {
                            println("Exit discovery")
                            running = false
                            break
                        } else {
                            println("Continue discover")
                            Thread.sleep(1000)
                        }
                    } catch (_: SocketTimeoutException) {
                        //Useless
                    }
                }
            }
        }
    }

    fun stop() {
        running = false
    }
}