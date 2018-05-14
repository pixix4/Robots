package de.westermann.robots.server.connection

import de.westermann.robots.datamodel.*
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.Json
import de.westermann.robots.datamodel.util.Track
import de.westermann.robots.datamodel.util.json
import de.westermann.robots.server.util.Protection
import io.javalin.embeddedserver.jetty.websocket.WsSession
import java.security.AccessControlException

/**
 * @author lars
 */
class WebSocketConnection {

    private data class Connection(
            val session: WsSession,
            val controller: Controller
    ) {

        var admin: Boolean = false
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

        val iClient = object : IClient {
            override fun addRobot(robot: Robot) {
                send(IClient::addRobot.name, robot.toJson())
            }

            override fun updateRobot(robot: Robot) {
                send(IClient::updateRobot.name, robot.toJson())
            }

            override fun removeRobot(robot: Robot) {
                send(IClient::removeRobot.name, robot.toJson())
            }

            override fun addController(controller: Controller) {
                send(IClient::addController.name, controller.toJson())
            }

            override fun updateController(controller: Controller) {
                send(IClient::updateController.name, controller.toJson())
            }

            override fun removeController(controller: Controller) {
                send(IClient::removeController.name, controller.toJson())
            }

            override fun bind(controllerId: Int, robotId: Int) {
                send(IClient::bind.name, json {
                    value("controllerId") { controllerId }
                    value("robotId") { robotId }
                })
            }

            override fun unbind(controllerId: Int, robotId: Int) {
                send(IClient::unbind.name, json {
                    value("controllerId") { controllerId }
                    value("robotId") { robotId }
                })
            }

            override fun login() {
                send(IClient::login.name)
            }

            override fun logout() {
                send(IClient::logout.name)
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

        val bindObserver = object: DeviceManager.OnBindChange {
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

        val iServer = object : IServer {
            override fun onTrack(track: Track) {
                controller.robots.forEach { it.track = track }
            }

            override fun onAbsoluteSpeed(speed: Double) {
                controller.robots.forEach { it.speed = speed }
            }

            override fun onRelativeSpeed(deltaSpeed: Double) {
                controller.robots.forEach { it.speed += deltaSpeed }
            }

            override fun onButton(button: Button) {
                TODO()
            }

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
            IServer::onTrack.name -> parsed?.let {
                connection.iServer.onTrack(Track.fromJson(it))
            }
            IServer::onAbsoluteSpeed.name -> data?.toString()?.toDoubleOrNull()?.let {
                connection.iServer.onAbsoluteSpeed(it)
            }
            IServer::onRelativeSpeed.name -> data?.toString()?.toDoubleOrNull()?.let {
                connection.iServer.onRelativeSpeed(it)
            }
            IServer::onButton.name -> parsed?.let {
                connection.iServer.onButton(Button.fromJson(it))
            }
            IServer::bind.name -> parsed?.let {
                connection.iServer.bind(
                        it["controllerId"]?.toString()?.toIntOrNull() ?: -1,
                        it["robotId"]?.toString()?.toIntOrNull() ?: -1
                )
            }
            IServer::unbind.name -> parsed?.let {
                connection.iServer.bind(
                        it["controllerId"]?.toString()?.toIntOrNull() ?: -1,
                        it["robotId"]?.toString()?.toIntOrNull() ?: -1
                )
            }
            IServer::login.name -> connection.iServer.login((data as? String) ?: "")
            IServer::logout.name -> connection.iServer.logout()
            else -> throw IllegalArgumentException("Cannot find function '$function' of client ${socket.remoteAddress}")
        }
    }
}