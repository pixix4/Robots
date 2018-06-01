package de.westermann.robots.website

import de.westermann.robots.datamodel.*
import de.westermann.robots.datamodel.observe.ObservableProperty
import de.westermann.robots.datamodel.util.*
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

    private var connection: WebSocket? = null

    private fun send(function: String, data: Any? = null) {
        try {
            connection?.send(
                    json {
                        value("function") { function }
                        value("param") { data }
                    }.also { it: Json ->
                        console.log("send", it.obj)
                    }.stringify()
            )
        } catch (_: Throwable) {
            println("Problem in web socket connection")
        }
    }

    private val iClient = object : IWebClient {
        override fun addRobot(robot: Robot) {
            DeviceManager.robots += robot

            registeredProperty.value = DeviceManager.robots.toSet().isNotEmpty()
        }

        override fun updateRobot(robot: Robot) {
            val r = DeviceManager.robots[robot.id]
            if (r == null) {
                DeviceManager.robots += robot
            } else {
                r.fromJson(robot.toJson())
            }

            registeredProperty.value = DeviceManager.robots.toSet().isNotEmpty()
        }

        override fun removeRobot(robot: Robot) {
            DeviceManager.robots -= robot.id

            registeredProperty.value = DeviceManager.robots.toSet().isNotEmpty()
        }

        override fun addController(controller: Controller) {
            DeviceManager.controllers += controller
        }

        override fun updateController(controller: Controller) {
            val c = DeviceManager.controllers[controller.id]
            if (c == null) {
                DeviceManager.controllers += controller
            } else {
                c.fromJson(controller.toJson())
            }
        }

        override fun removeController(controller: Controller) {
            DeviceManager.controllers -= controller.id
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

    val iController = object : IController {

        override fun onTrack(track: Track) {
            send(IController::onTrack.name, track.toJson())
        }

        override fun onAbsoluteSpeed(speed: Double) {
            send(IController::onAbsoluteSpeed.name, speed)
        }

        override fun onRelativeSpeed(deltaSpeed: Double) {
            send(IController::onRelativeSpeed.name, deltaSpeed)
        }

        override fun onButton(button: Button) {
            send(IController::onButton.name, button.toJson())
        }

        override fun name(name: String) {
            send(IController::name.name, name)
        }
    }

    val iServer = object : IWebServer {

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

        override fun setControllerName(controllerId: Int, name: String) {
            send(IWebServer::setControllerName.name, json {
                value("controllerId") { controllerId }
                value("name") { name }
            })
        }

        override fun setRobotName(robotId: Int, name: String) {
            send(IWebServer::setRobotName.name, json {
                value("robotId") { robotId }
                value("name") { name }
            })
        }

        override fun setColor(robotId: Int, color: Color) {
            send(IWebServer::setColor.name, json {
                value("robotId") { robotId }
                value("color") { color.toString() }
            })
        }

        override fun setWhitePoint(robotId: Int, color: Color) {
            send(IWebServer::setWhitePoint.name, json {
                value("robotId") { robotId }
                value("color") { color.toString() }
            })
        }

        override fun setBlackPoint(robotId: Int, color: Color) {
            send(IWebServer::setBlackPoint.name, json {
                value("robotId") { robotId }
                value("color") { color.toString() }
            })
        }

        override fun setForeground(robotId: Int) {
            send(IWebServer::setForeground.name, json {
                value("robotId") { robotId }
            })
        }

        override fun setBackground(robotId: Int) {
            send(IWebServer::setBackground.name, json {
                value("robotId") { robotId }
            })
        }

        override fun setPid(robotId: Int, state: Boolean) {
            send(IWebServer::setPid.name, json {
                value("robotId") { robotId }
                value("state") { state.toString() }
            })
        }
    }

    val adminProperty = ObservableProperty(false)
    val connectedProperty = ObservableProperty(false)
    val registeredProperty = ObservableProperty(false)

    var connectTimeout: Int? = -1

    fun connect() {
        if (connection == null && connectTimeout != null) {
            connection = createWebSocket()
        }
    }

    fun stop() {
        connectTimeout?.let {
            window.clearTimeout(it)
        }
        connectTimeout = null
    }

    private var intervalId: Int? = null

    private fun createWebSocket() = WebSocket(url).also { ws ->
        ws.onopen = {
            connectedProperty.value = true
            intervalId = window.setInterval({
                ws.send("ping")
            }, 5000)
            iController.name(window.navigator.userAgent)
            Unit //This is weird
        }
        ws.onclose = {
            connectedProperty.value = false
            connection = null
            intervalId?.let {
                window.clearInterval(it)
                intervalId = null
            }

            if (connectTimeout != null) {
                connectTimeout = window.setTimeout({
                    connect()
                }, 1000)
            }
        }
        ws.onmessage = { event ->
            ((event as? MessageEvent)?.data as? String)?.let { str ->
                if (str == "pong")
                    return@let null

                val json = Json.fromString(str).also {
                    console.log("receive", it.obj)
                }
                val function = json["function"] as? String
                //val data = json["param"]
                val parsed = json.json("param")

                when (function) {
                    IWebClient::addRobot.name -> parsed?.let { iClient.addRobot(Robot.fromJson(it)) }
                    IWebClient::updateRobot.name -> parsed?.let { iClient.updateRobot(Robot.fromJson(it)) }
                    IWebClient::removeRobot.name -> parsed?.let { iClient.removeRobot(Robot.fromJson(it)) }

                    IWebClient::addController.name -> parsed?.let { iClient.addController(Controller.fromJson(it)) }
                    IWebClient::updateController.name -> parsed?.let { iClient.updateController(Controller.fromJson(it)) }
                    IWebClient::removeController.name -> parsed?.let { iClient.removeController(Controller.fromJson(it)) }

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