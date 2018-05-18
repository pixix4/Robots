package de.westermann.robots.website

import de.westermann.robots.datamodel.*
import de.westermann.robots.datamodel.observe.ObservableProperty
import de.westermann.robots.datamodel.observe.accessor
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
            )
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

    private val iClient = object : IWebClient {
        override fun addRobot(robot: Robot) {
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
            adminProperty.value = true
        }

        override fun logout() {
            adminProperty.value = false
        }
    }

    val iServer = object : IWebServer {
        override fun onTrack(track: Track) {
            send(IWebServer::onTrack.name, track.toJson())
        }

        override fun onAbsoluteSpeed(speed: Double) {
            send(IWebServer::onAbsoluteSpeed.name, speed)
        }

        override fun onRelativeSpeed(deltaSpeed: Double) {
            send(IWebServer::onRelativeSpeed.name, deltaSpeed)
        }

        override fun onButton(button: Button) {
            send(IWebServer::onButton.name, button.toJson())
        }

        override fun bind(controllerId: Int, robotId: Int) {
            send(IWebServer::bind.name, json {
                value("controllerId") { controllerId }
                value("robotId") { robotId }
            })
        }

        override fun unbind(controllerId: Int, robotId: Int) {
            send(IWebServer::unbind.name, json {
                value("controllerId") { controllerId }
                value("robotId") { robotId }
            })
        }

        override fun login(password: String) {
            send(IWebServer::login.name, password)
        }

        override fun logout() {
            send(IWebServer::logout.name)
        }
    }

    val adminProperty = ObservableProperty(false)
    val admin by adminProperty.accessor()

    private var onOpen: () -> Unit = {}

    fun connect(onOpen: (() -> Unit)? = null) {
        if (onOpen != null) {
            if (firstConnect) {
                this.onOpen = onOpen
            } else {
                onOpen()
            }
        }

        if (closed) {
            connection = createWebSocket()
            closed = false
        }
    }

    private var intervalId: Int? = null

    private fun createWebSocket() = WebSocket(url).also {ws ->
        ws.onopen = {
            if (firstConnect) {
                onOpen()
                firstConnect = false
            }
            intervalId = window.setInterval({
                ws.send("ping")
            }, 5000)
            Unit //This is weird
        }
        ws.onclose = {
            closed = true
            intervalId?.let {
                window.clearInterval(it)
                intervalId = null
            }
            connect()
        }
        ws.onmessage = { event ->
            ((event as? MessageEvent)?.data as? String)?.let { str ->
                if (str == "pong")
                    Unit

                val json = Json.fromString(str)
                val function = json["function"] as? String
                //val data = json["param"]
                val parsed = json.json("param")

                when (function) {
                    IWebClient::addRobot.name -> parsed?.let { iClient.addRobot(Robot.fromJson(it)) }

                    IWebClient::updateRobot.name -> parsed?.let { iClient.updateRobot(Robot.fromJson(it)) }
                    IWebClient::removeRobot.name -> parsed?.let { iClient.removeRobot(Robot.fromJson(it)) }
                    IWebClient::addController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IWebClient::updateController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IWebClient::removeController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IWebClient::bind.name -> {
                        val robotId = parsed?.get("robotId")?.toString()?.toIntOrNull()
                        val controllerId = parsed?.get("controllerId")?.toString()?.toIntOrNull()
                        if (robotId != null && controllerId != null) {
                            iClient.bind(controllerId, robotId)
                        }
                    }
                    IWebClient::unbind.name -> {
                        val robotId = parsed?.get("robotId")?.toString()?.toIntOrNull()
                        val controllerId = parsed?.get("controllerId")?.toString()?.toIntOrNull()
                        if (robotId != null && controllerId != null) {
                            iClient.unbind(controllerId, robotId)
                        }
                    }
                    IWebClient::login.name -> iClient.login()
                    IWebClient::logout.name -> iClient.logout()
                    else -> throw IllegalArgumentException("Cannot find function '$function'")
                }
                Unit //This is weird
            }
        }
    }
}