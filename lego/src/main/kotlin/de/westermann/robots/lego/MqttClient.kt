package de.westermann.robots.lego

import de.westermann.robots.datamodel.IRobotClient
import de.westermann.robots.datamodel.IRobotServer
import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Track
import de.westermann.robots.datamodel.util.Version
import de.westermann.robots.robot.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.MqttClient
import java.lang.reflect.Proxy
import java.net.InetAddress
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * @author lars
 */
object MqttClient {
    val running: Boolean
        get() = mqtt?.isConnected ?: false

    private var mqtt: MqttClient? = null
    private var executor: ScheduledExecutorService? = null

    private fun addressToString(address: InetAddress, port: Int): String =
            "tcp://" + "$address:$port".replace("/", "")

    fun start(address: InetAddress, port: Int, onDisconnect: () -> Unit): Boolean {
        if (running) return true

        println("Connect to ${address.toString().replace("/", "")}:$port")

        try {
            mqtt = MqttClient(
                    addressToString(address, port),
                    MqttClient.generateClientId(),
                    null
            ).also {
                it.setCallback(object : MqttCallback {
                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        val data = message ?: return
                        try {
                            decodeMqtt(iClient, data.payload.toStringList())
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    override fun connectionLost(cause: Throwable?) {
                        stop()
                        onDisconnect()
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        //TODO
                    }

                })
                val options = MqttConnectOptions()
                it.connect(options)

                it.subscribe(it.clientId)
                iServer.version(Version.parse(Environment.Build.version))

                executor = Executors.newSingleThreadScheduledExecutor().also {
                    it.scheduleAtFixedRate({
                        iServer.currentColor(Devices.colorSensor.color)
                    }, 0, 1, TimeUnit.SECONDS)
                }
            }

        } catch (_: Exception) {
            return false
        }
        return true
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

        override fun track(track: Track) {
            Driver.drive(track)
        }

        override fun trim(trim: Double) {
            Driver.trim = trim
        }

    }

    val iServer = Proxy.newProxyInstance(
            MqttClient::class.java.classLoader,
            arrayOf(IRobotServer::class.java)
    ) { _, method, params ->
        send(encodeMqtt(method, params.toList()))
    } as IRobotServer

    private fun send(params: List<String>) {
        mqtt?.let {
            it.publish(it.clientId, MqttMessage(params.toByteArray()))
        }
    }

    fun stop() {
        if (mqtt?.isConnected == true) {
            mqtt?.disconnect()
            executor?.shutdown()
        }
        mqtt = null
        executor = null
    }
}