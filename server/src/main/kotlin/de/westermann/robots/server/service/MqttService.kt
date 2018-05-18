package de.westermann.robots.server.service

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.*
import de.westermann.robots.robot.*
import de.westermann.robots.server.util.Configuration
import io.moquette.interception.AbstractInterceptHandler
import io.moquette.interception.messages.*
import io.moquette.server.Server
import io.netty.buffer.Unpooled
import io.netty.handler.codec.mqtt.MqttMessageBuilders
import io.netty.handler.codec.mqtt.MqttQoS
import mu.KotlinLogging
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

    private data class Helper(
            val robot: Robot,
            val iServer: IRobotServer
    )

    private var robots = mapOf<String, Helper>()

    override fun start() {
        start(Configuration.properties.robotPort)
    }

    private fun addRobot(id: String) {
        val robot = Robot(DeviceManager.robots.nextId)
        DeviceManager.robots += robot

        val iRobotClient = Proxy.newProxyInstance(
                MqttService::class.java.classLoader,
                arrayOf(IRobotServer::class.java)
        ) { _, method, params ->
            server?.internalPublish(
                    MqttMessageBuilders.publish()
                            .topicName(id)
                            .qos(MqttQoS.AT_LEAST_ONCE)
                            .retained(true)
                            .payload(Unpooled.copiedBuffer(encodeMqtt(method, params.toList()).toByteArray()))
                            .build(),
                    id
            )
        } as IRobotClient

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
            if (newValue.foreground != oldValue.foreground) {
                iRobotClient.foregroundColor(newValue.foreground)
            }
            if (newValue.background != oldValue.background) {
                iRobotClient.backgroundColor(newValue.background)
            }
        }

        robots += id to Helper(
                robot,
                object : IRobotServer {
                    override fun map(points: List<Coordinate>) {
                        //TODO
                    }

                    override fun currentColor(color: Color) {
                        robot.color = color
                    }

                    override fun foregroundColor(color: Color) {
                        robot.lineFollower = robot.lineFollower.copy(foreground = color)
                    }

                    override fun backgroundColor(color: Color) {
                        robot.lineFollower = robot.lineFollower.copy(background = color)
                    }

                    override fun energy(energy: Energy) {
                        robot.energy = energy
                    }

                    override fun version(version: Version) {
                        robot.version = version
                    }
                }
        )
    }

    private fun removeRobot(id: String) {
        val helper = robots[id] ?: return
        DeviceManager.robots -= helper.robot
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
                    val helper = robots[msg.clientID] ?: return
                    val exec = decodeMqtt(IRobotServer::class, msg.payload.array().toStringList()) ?: return
                    exec.first.call(helper.iServer, *exec.second)
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
}