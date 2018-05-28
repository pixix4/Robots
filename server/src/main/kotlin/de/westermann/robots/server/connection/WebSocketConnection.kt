package de.westermann.robots.server.connection

import de.westermann.robots.datamodel.*
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.datamodel.util.*
import de.westermann.robots.server.util.Protection
import io.javalin.embeddedserver.jetty.websocket.WsSession
import mu.KotlinLogging
import java.security.AccessControlException

/**
 * @author lars
 */
class WebSocketConnection {

    val logger = KotlinLogging.logger {}

    private data class Connection(
            val session: WsSession,
            val controller: Controller
    ) {

        var admin: Boolean = !Protection.loginRequired
            set(value) {
                val init = value && !field
                field = value
                if (init) initAdmin()
            }

        fun initAdmin() {
            DeviceManager.robots.forEach { robot ->
                iClient.addRobot(robot)
            }
            DeviceManager.controllers.forEach { controller ->
                iClient.addController(controller)
                controller.robots.forEach { robot ->
                    iClient.bind(controller.id, robot.id)
                }
            }
        }

        private fun send(function: String, json: Json? = null) {
            session.send(
                    json {
                        value("function") { function }
                        value("param") { json }
                    }.stringify()
            )
        }

        val iClient = object : IWebClient {
            override fun addRobot(robot: Robot) {
                send(IWebClient::addRobot.name, robot.toJson())
            }

            override fun updateRobot(robot: Robot) {
                send(IWebClient::updateRobot.name, robot.toJson())
            }

            override fun removeRobot(robot: Robot) {
                send(IWebClient::removeRobot.name, robot.toJson())
            }

            override fun addController(controller: Controller) {
                send(IWebClient::addController.name, controller.toJson())
            }

            override fun updateController(controller: Controller) {
                send(IWebClient::updateController.name, controller.toJson())
            }

            override fun removeController(controller: Controller) {
                send(IWebClient::removeController.name, controller.toJson())
            }

            override fun bind(controllerId: Int, robotId: Int) {
                send(IWebClient::bind.name, json {
                    value("controllerId") { controllerId }
                    value("robotId") { robotId }
                })
            }

            override fun unbind(controllerId: Int, robotId: Int) {
                send(IWebClient::unbind.name, json {
                    value("controllerId") { controllerId }
                    value("robotId") { robotId }
                })
            }

            override fun login() {
                send(IWebClient::login.name)
            }

            override fun logout() {
                send(IWebClient::logout.name)
            }
        }

        val robotObserver = object : Library.Observer<Robot> {
            override fun onAdd(element: Robot) {
                if (admin || element in controller.robots) {
                    iClient.addRobot(element)
                }
            }

            override fun onChange(element: Robot) {
                if (admin || element in controller.robots) {
                    iClient.updateRobot(element)
                }
            }

            override fun onRemove(element: Robot) {
                if (admin || element in controller.robots) {
                    iClient.removeRobot(element)
                }
            }
        }

        val controllerObserver = object : Library.Observer<Controller> {
            override fun onAdd(element: Controller) {
                if (admin || element == controller) {
                    iClient.addController(element)
                }
            }

            override fun onChange(element: Controller) {
                if (admin || element == controller) {
                    iClient.updateController(element)
                }
            }

            override fun onRemove(element: Controller) {
                if (admin || element == controller) {
                    iClient.removeController(element)
                }
            }
        }

        val bindObserver = object : DeviceManager.OnBindChange {
            override fun onBind(controller: Controller, robot: Robot) {
                if (admin || controller == this@Connection.controller || robot in this@Connection.controller.robots) {
                    iClient.bind(controller.id, robot.id)
                }
            }

            override fun onUnbind(controller: Controller, robot: Robot) {
                if (admin || controller == this@Connection.controller || robot in this@Connection.controller.robots) {
                    iClient.bind(controller.id, robot.id)
                }
            }

        }

        fun removeListeners() {
            DeviceManager.robots.removeObserver(robotObserver)
            DeviceManager.controllers.removeObserver(controllerObserver)
            DeviceManager.removeOnBindChange(bindObserver)
        }

        fun initListeners() {
            DeviceManager.robots.onChange(robotObserver)
            DeviceManager.controllers.onChange(controllerObserver)
            DeviceManager.onBindChange(bindObserver)
        }

        val iServer = object : IWebServer {
            override fun bind(controllerId: Int, robotId: Int) {
                if (admin) {
                    val controller = DeviceManager.controllers[controllerId]
                    val robot = DeviceManager.robots[robotId]
                    if (controller != null && robot != null) {
                        DeviceManager.bindController(controller, robot)
                    }
                }
            }

            override fun unbind(controllerId: Int, robotId: Int) {
                if (admin) {
                    val controller = DeviceManager.controllers[controllerId]
                    val robot = DeviceManager.robots[robotId]
                    if (controller != null && robot != null) {
                        DeviceManager.unbindController(controller, robot)
                    }
                } else {
                    throw AccessControlException("")
                }
            }

            override fun login(password: String) {
                if (!admin) {
                    admin = Protection.login(password)
                    if (admin) {
                        iClient.login()
                    } else {
                        iClient.logout()
                    }
                }
            }

            override fun logout() {
                admin = false
                iClient.logout()
            }

            override fun setName(robotId: Int, name: String) {
                DeviceManager.robots[robotId]?.name = name
            }

            override fun setColor(robotId: Int, color: Color) {
                DeviceManager.robots[robotId]?.color = color
            }
        }

        init {
            if (admin) {
                initAdmin()
            }
        }
    }

    private var connections = emptyMap<WsSession, Connection>()

    operator fun plusAssign(socket: WsSession) {
        Controller(DeviceManager.controllers.nextId).let { controller ->
            DeviceManager.controllers += controller
            connections += socket to Connection(socket, controller).also {
                it.initListeners()
            }
        }
    }

    operator fun minusAssign(socket: WsSession) {
        connections[socket]?.let { connection ->
            connection.removeListeners()
            DeviceManager.controllers -= connection.controller
            connections -= socket
        }
    }

    fun execute(socket: WsSession, json: Json) {
        val connection = connections[socket] ?: throw AccessControlException("Unknown socket")
        val function = json["function"]
        val data = json["param"]
        val parsed = json.json("param")

        when (function) {
            IController::onTrack.name -> parsed?.let {
                connection.controller.iController?.onTrack(Track.fromJson(it))
            }
            IController::onAbsoluteSpeed.name -> data?.toString()?.toDoubleOrNull()?.let {
                connection.controller.iController?.onAbsoluteSpeed(it)
            }
            IController::onRelativeSpeed.name -> data?.toString()?.toDoubleOrNull()?.let {
                connection.controller.iController?.onRelativeSpeed(it)
            }
            IController::onButton.name -> parsed?.let {
                connection.controller.iController?.onButton(Button.fromJson(it))
            }
            IController::name.name -> data?.toString()?.let {
                connection.controller.iController?.name(it)
            }
            IWebServer::bind.name -> parsed?.let {
                connection.iServer.bind(
                        it["controllerId"]?.toString()?.toIntOrNull() ?: -1,
                        it["robotId"]?.toString()?.toIntOrNull() ?: -1
                )
            }
            IWebServer::unbind.name -> parsed?.let {
                connection.iServer.bind(
                        it["controllerId"]?.toString()?.toIntOrNull() ?: -1,
                        it["robotId"]?.toString()?.toIntOrNull() ?: -1
                )
            }
            IWebServer::setName.name -> parsed?.let {
                connection.iServer.setName(
                        it["robotId"]?.toString()?.toIntOrNull() ?: -1,
                        it["name"]?.toString() ?: ""
                )
            }
            IWebServer::setColor.name -> parsed?.let {
                connection.iServer.setColor(
                        it["robotId"]?.toString()?.toIntOrNull() ?: -1,
                        it["color"]?.toString()?.let {
                            try {
                                Color.parse(it)
                            } catch (_: IllegalArgumentException) {
                                null
                            }
                        } ?: Color.TRANSPARENT
                )
            }
            IWebServer::login.name -> connection.iServer.login((data as? String) ?: "")
            IWebServer::logout.name -> connection.iServer.logout()
            else -> logger.warn("Cannot find function '$function' of client ${socket.remoteAddress}")
        }
    }
}