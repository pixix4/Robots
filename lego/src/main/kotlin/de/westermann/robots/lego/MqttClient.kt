package de.westermann.robots.lego

import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Track
import de.westermann.robots.robot.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttClient
import java.lang.reflect.Proxy
import java.net.InetAddress
import java.net.InetSocketAddress

/**
 * @author lars
 */
object MqttClient {
    val running: Boolean
        get() = mqtt?.isConnected ?: false

    private var mqtt: MqttClient? = null

    fun start(address: InetAddress, port: Int) {
        if (running) return

        mqtt = MqttClient(
                InetSocketAddress(address, port).toString(),
                MqttAsyncClient.generateClientId()
        )
        mqtt?.let {
            it.setCallback(object : MqttCallback {
                override fun messageArrived(topic: String?, message: MqttMessage?) {
                    val data = message ?: return
                    val exec = decodeMqtt(IRobotClient::class, data.payload.toStringList()) ?: return
                    exec.first.call(iClient, *exec.second)
                }

                override fun connectionLost(cause: Throwable?) {
                    stop()
                    start(address, port)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken?) {
                    TODO("not implemented")
                }

            })
            val options = MqttConnectOptions()
            it.connect(options)

        }
    }

    val iClient = object : IRobotClient {
        override fun foregroundColor(color: Color?) {
            if (color == null) {
                PidController.foreground()
            } else {
                PidController.foreground(color.red, color.green, color.blue)
            }
        }

        override fun backgroundColor(color: Color?) {
            if (color == null) {
                PidController.background()
            } else {
                PidController.background(color.red, color.green, color.blue)
            }
        }

        override fun resetMap() {
            TODO("not implemented")
        }

        override fun pid(enable: Boolean) {
            if (enable) {
                PidController.start()
            } else {
                PidController.stop()
            }
        }

        override fun speed(speed: Double) {
            Driver.speed = speed
        }

        override fun motors(track: Track) {
            Driver.drive(track)
        }

        override fun trim(trim: Double) {
           Driver.trim = trim
        }

    }

    val iServer = Proxy.newProxyInstance(
            MqttClient::class.java.classLoader,
            arrayOf(IRobotServer::class.java)
    ) { obj, method, params ->
        send(encodeMqtt(method, params.toList()))
    }

    private fun send(params: List<String>) {
        mqtt?.let {
            it.publish(it.clientId, MqttMessage(params.toByteArray()))
        }
    }

    fun stop() {
        mqtt?.disconnect()
        mqtt = null
    }
}