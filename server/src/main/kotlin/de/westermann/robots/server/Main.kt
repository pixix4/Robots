package de.westermann.robots.server

import de.westermann.robots.datamodel.*
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.datamodel.util.*
import de.westermann.robots.server.service.*
import de.westermann.robots.server.util.Configuration
import mu.KotlinLogging
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min
import kotlin.system.exitProcess

object Main {
    private val logger = KotlinLogging.logger {}

    @JvmStatic
    fun main(args: Array<String>) {
        if ("--help" in args) {
            Configuration.help {
                println(it)
            }
            exitProcess(0)
        }

        Configuration.load(args.toList())

        Configuration.tmpClear()
        logger.info { "Temp folder is ${Configuration.properties.tmpDirectory.toAbsolutePath()}" }

        DeviceManager.controllers.onChange(object : Library.Observer<Controller> {
            override fun onAdd(element: Controller) {
                element.iController = object : IController {

                    override fun onTrack(track: Track) {
                        element.robots.forEach { it.track = track }
                    }

                    override fun onAbsoluteSpeed(speed: Double) {
                        element.robots.forEach { it.speed = speed }
                    }

                    override fun onRelativeSpeed(deltaSpeed: Double) {
                        element.robots.forEach { it.speed = min(1.0, max(0.0, it.speed + deltaSpeed)) }
                    }

                    override fun onButton(button: Button) {
                        element.robots.forEach { it.button.fire(button) }
                    }

                    override fun name(name: String) {
                        element.name = name
                        if (element.type == Controller.Type.UNKNOWN) {
                            Controller.Type.detect(name).let {
                                if (it != Controller.Type.UNKNOWN) {
                                    element.type = it
                                }
                            }
                        }
                    }
                }
            }
        })

        DeviceManager.robots.onChange(object : Library.Observer<Robot> {
            override fun onAdd(element: Robot) {
                element.iRobotServer = object : IRobotServer {
                    override fun currentPosition(pos: Coordinate) {
                        element.map = element.map + pos
                    }

                    override fun currentColor(color: Color) {
                        element.visibleColor = color
                    }

                    override fun foregroundColor(color: Color) {
                        element.lineFollower = element.lineFollower.copy(foreground = color)
                        if (element.lineFollower.state == LineFollower.State.UNAVAILABLE) {
                            element.lineFollower = element.lineFollower.copy(state = LineFollower.State.DISABLED)
                        }
                    }

                    override fun backgroundColor(color: Color) {
                        element.lineFollower = element.lineFollower.copy(background = color)
                        if (element.lineFollower.state == LineFollower.State.UNAVAILABLE) {
                            element.lineFollower = element.lineFollower.copy(state = LineFollower.State.DISABLED)
                        }
                    }

                    override fun energy(energy: Energy) {
                        element.energy = energy
                    }

                    override fun version(version: Version) {
                        element.version = version
                    }

                    override fun name(name: String) {
                        element.name = name
                    }

                    override fun color(color: Color) {
                        element.color = color
                    }

                    override fun availableColors(colors: List<Color>) {
                        element.availableColors = colors
                    }
                }
            }
        })

        if (Configuration.properties.demo) {
            logger.warn { "Enter demo mode" }
            Demo.load()
        }

        DiscoveryService.start(Configuration.properties.discoveryPort)
        MqttService.start(Configuration.properties.robotPort)
        WebService.start(Configuration.properties.webPort)
        GamepadService.start()
        ReplService.start()

        Runtime.getRuntime().addShutdownHook(thread(start = false, name = "shutdown") {
            logger.info { "Stopping..." }

            Configuration.tmpClear()
            DiscoveryService.stop()
            ReplService.stop()
            WebService.stop()
            GamepadService.stop()
            MqttService.stop()
        })
    }

    fun stop() {
        exitProcess(0)
    }
}

