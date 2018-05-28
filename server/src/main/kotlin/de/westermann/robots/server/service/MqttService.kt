package de.westermann.robots.server.service

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.IRobotClient
import de.westermann.robots.datamodel.IRobotServer
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.LineFollower
import de.westermann.robots.robot.decodeMqtt
import de.westermann.robots.robot.encodeMqtt
import de.westermann.robots.robot.toByteArray
import de.westermann.robots.robot.toStringList
import de.westermann.robots.server.util.Configuration
import io.moquette.interception.AbstractInterceptHandler
import io.moquette.interception.messages.*
import io.moquette.server.Server
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.handler.codec.mqtt.MqttMessageBuilders
import io.netty.handler.codec.mqtt.MqttQoS
import mu.KotlinLogging
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Proxy
import java.util.*


/**
 * @author lars
 */
object MqttService : Service {

    val logger = KotlinLogging.logger {}
    override val running: Boolean
        get() = server != null
    var server: Server? = null


    private var robots = mapOf<String, Robot>()

    override fun start() {
        start(Configuration.properties.robotPort)
    }

    private fun addRobot(id: String) {
        if (robots.containsKey(id)) return
        val robot = Robot(DeviceManager.robots.nextId)
        DeviceManager.robots += robot

        val iRobotClient = Proxy.newProxyInstance(
                MqttService::class.java.classLoader,
                arrayOf(IRobotClient::class.java)
        ) { _, method, params ->
            server?.internalPublish(
                    MqttMessageBuilders.publish()
                            .topicName(id)
                            .qos(MqttQoS.AT_LEAST_ONCE)
                            .payload(Unpooled.copiedBuffer(
                                    encodeMqtt(method, params?.toList() ?: emptyList()).toByteArray()
                            ))
                            .build(),
                    id
            )
        } as IRobotClient

        robot.nameProperty.onChange { newValue, _ ->
            iRobotClient.setName(newValue)
        }
        robot.colorProperty.onChange { newValue, _ ->
            iRobotClient.setColor(newValue)
        }
        robot.trackProperty.onChange { newValue, _ ->
            iRobotClient.track(newValue)
        }
        robot.speedProperty.onChange { newValue, _ ->
            iRobotClient.speed(newValue)
        }
        robot.trimProperty.onChange { newValue, _ ->
            iRobotClient.trim(newValue)
        }
        robot.lineFollowerPropety.onChange { newValue, oldValue ->
            if (newValue.state != oldValue.state) {
                iRobotClient.pid(newValue.state == LineFollower.State.RUNNING)
            }
        }
        robot.button.on { button ->
            when (button.type) {
                Button.Type.A -> {
                    if (button.isDown) {
                        iRobotClient.kick()
                    }
                }
                Button.Type.B -> if (button.isDown) {
                    when {
                        robot.lineFollower.state == LineFollower.State.RUNNING -> {
                            robot.lineFollower = robot.lineFollower.copy(state = LineFollower.State.DISABLED)
                        }
                        robot.lineFollower.state == LineFollower.State.DISABLED -> {
                            robot.lineFollower = robot.lineFollower.copy(state = LineFollower.State.RUNNING)
                        }
                        else -> {
                        }
                    }
                }
                else -> {
                }
            }
        }

        robots += id to robot
    }

    private fun removeRobot(id: String) {
        val robot = robots[id] ?: return
        DeviceManager.robots -= robot
        robots -= id
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
                override fun getID(): String = "RobotMqttServer"

                override fun onPublish(msg: InterceptPublishMessage?) {
                    if (msg == null) return
                    val robot = robots[msg.clientID] ?: return
                    try {
                        val exec =
                                decodeMqtt(IRobotServer::class, msg.payload.toByteArraySafe().toStringList().also {
                                    println("Recv: $it")
                                }) ?: return
                        exec.first.call(robot.iRobotServer, *exec.second)
                    } catch (e: InvocationTargetException) {
                        logger.warn(
                                "Cannot invoke mqtt message: ${msg.payload.toByteArraySafe().toStringList()}", e.cause
                        )
                    }
                }

                override fun onSubscribe(msg: InterceptSubscribeMessage?) {
                    if (msg == null) return
                }

                override fun onUnsubscribe(msg: InterceptUnsubscribeMessage?) {
                    if (msg == null) return
                }

                override fun onConnect(msg: InterceptConnectMessage?) {
                    if (msg == null) return
                    addRobot(msg.clientID)
                }

                override fun onConnectionLost(msg: InterceptConnectionLostMessage?) {
                    if (msg == null) return
                    removeRobot(msg.clientID)
                }

                override fun onDisconnect(msg: InterceptDisconnectMessage?) {
                    if (msg == null) return
                    removeRobot(msg.clientID)
                }
            })
        }
    }

    override fun stop() {
        server?.stopServer()
        server = null
    }

    fun ByteBuf.toByteArraySafe(): ByteArray {
        if (this.hasArray()) {
            return this.array()
        }

        val bytes = ByteArray(this.readableBytes())
        this.getBytes(this.readerIndex(), bytes)

        return bytes
    }
}