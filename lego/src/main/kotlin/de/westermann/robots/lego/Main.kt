package de.westermann.robots.lego

import org.eclipse.paho.client.mqttv3.*
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class Main {
    companion object {

        /**
         * This is the main entry point
         */
        @JvmStatic
        fun main(args: Array<String>) {
            Runtime.getRuntime().addShutdownHook(thread(start = false, name = "shutdown") {
                DiscoveryClient.stop()
                MqttClient.stop()

                Devices.leftMotor.stop()
                Devices.rightMotor.stop()
                Devices.extraMotor.stop()
            })

            findAndConnect()

            while (true) {}
        }

        const val port:Int = 7500

        fun findAndConnect() {
            DiscoveryClient.find(port) {address, port ->
                MqttClient.start(address, port) {
                    println("------ Connection lost ------")
                    findAndConnect()
                }
            }
        }
    }
}
