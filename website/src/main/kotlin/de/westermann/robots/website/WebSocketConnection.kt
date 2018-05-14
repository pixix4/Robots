package de.westermann.robots.website

import de.westermann.robots.datamodel.*
import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.Json
import de.westermann.robots.datamodel.util.Track
import de.westermann.robots.datamodel.util.json
import org.w3c.dom.MessageEvent
import org.w3c.dom.WebSocket
import kotlin.browser.window

/**
 * @author lars
 */
object WebSocketConnection {
    private val url = (
            "ws" + window.location.protocol.drop(4).dropLast(1) + "://" +
                    window.location.hostname + window.location.port.let {
                if (it.isEmpty()) "" else ":$it"
            } + "/ws"
            ).also { println(it) }
    private var firstConnect = true
    private var closed = false
    private var connection: WebSocket = createWebSocket()

    private fun send(function: String, data: Any? = null) {
        connection.send(
                json {
                    value("function") { function }
                    value("param") { data }
                }.stringify()
        )
    }

    private val iClient = object : IClient {
        override fun addRobot(robot: Robot) {
            println("Add robot $robot")
            DeviceManager.robots += robot
        }

        override fun updateRobot(robot: Robot) {
            DeviceManager.controllers[robot.id]?.fromJson(robot.toJson())
        }

        override fun removeRobot(robot: Robot) {
            DeviceManager.robots -= robot
        }

        override fun addController(controller: Controller) {
            DeviceManager.controllers += controller
        }

        override fun updateController(controller: Controller) {
            DeviceManager.controllers[controller.id]?.fromJson(controller.toJson())
        }

        override fun removeController(controller: Controller) {
            DeviceManager.controllers -= controller
        }

        override fun bind(controllerId: Int, robotId: Int) {
            val controller = DeviceManager.controllers[controllerId]
            val robot = DeviceManager.robots[robotId]
            if (controller != null && robot != null) {
                DeviceManager.bindController(controller, robot)
            }
        }

        override fun unbind(controllerId: Int, robotId: Int) {
            val controller = DeviceManager.controllers[controllerId]
            val robot = DeviceManager.robots[robotId]
            if (controller != null && robot != null) {
                DeviceManager.unbindController(controller, robot)
            }
        }

        override fun login() {
            println("login")
        }

        override fun logout() {
            println("logout")
        }
    }

    val iServer = object : IServer {
        override fun onTrack(track: Track) {
            send(IServer::onTrack.name, track.toJson())
        }

        override fun onAbsoluteSpeed(speed: Double) {
            send(IServer::onAbsoluteSpeed.name, speed)
        }

        override fun onRelativeSpeed(deltaSpeed: Double) {
            send(IServer::onRelativeSpeed.name, deltaSpeed)
        }

        override fun onButton(button: Button) {
            send(IServer::onButton.name, button.toJson())
        }

        override fun bind(controllerId: Int, robotId: Int) {
            send(IServer::bind.name, json {
                value("controllerId") { controllerId }
                value("robotId") { robotId }
            })
        }

        override fun unbind(controllerId: Int, robotId: Int) {
            send(IServer::unbind.name, json {
                value("controllerId") { controllerId }
                value("robotId") { robotId }
            })
        }

        override fun login(password: String) {
            send(IServer::login.name, password)
        }

        override fun logout() {
            send(IServer::logout.name)
        }
    }

    private var onopen: () -> Unit = {}

    fun connect(onopen: (() -> Unit)? = null) {
        if (onopen != null) {
            if (firstConnect) {
                this.onopen = onopen
            } else {
                onopen()
            }
        }

        if (closed) {
            connection = createWebSocket()
            closed = false
        }
    }

    private fun createWebSocket() = WebSocket(url).also {
        it.onopen = {
            println("open")
            if (firstConnect) {
                onopen()
                firstConnect = false
            }
        }
        it.onclose = {
            closed = true
            println("close")
            connect()
        }
        it.onmessage = { event ->
            ((event as? MessageEvent)?.data as? String)?.let { str ->
                val json = Json.fromString(str)
                val function = json["function"] as? String
                val data = json["param"]
                val parsed = json.json("param")

                when (function) {
                    IClient::addRobot.name -> parsed?.let { iClient.addRobot(Robot.fromJson(it)) }

                    IClient::updateRobot.name -> parsed?.let { iClient.updateRobot(Robot.fromJson(it)) }
                    IClient::removeRobot.name -> parsed?.let { iClient.removeRobot(Robot.fromJson(it)) }
                    IClient::addController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IClient::updateController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IClient::removeController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IClient::bind.name -> {
                        val robotId = parsed?.get("robotId")?.toString()?.toIntOrNull()
                        val controllerId = parsed?.get("controllerId")?.toString()?.toIntOrNull()
                        if (robotId != null && controllerId != null) {
                            iClient.bind(controllerId, robotId)
                        }
                    }
                    IClient::unbind.name -> {
                        val robotId = parsed?.get("robotId")?.toString()?.toIntOrNull()
                        val controllerId = parsed?.get("controllerId")?.toString()?.toIntOrNull()
                        if (robotId != null && controllerId != null) {
                            iClient.unbind(controllerId, robotId)
                        }
                    }
                    IClient::login.name -> iClient.login()
                    IClient::logout.name -> iClient.logout()
                    else -> throw IllegalArgumentException("Cannot find function '$function'")
                }
                Unit //This is weird
            }
        }
    }
}