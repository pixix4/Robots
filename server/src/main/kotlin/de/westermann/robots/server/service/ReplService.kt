package de.westermann.robots.server.service

import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.server.util.Environment
import de.westermann.robots.server.util.Printer
import de.westermann.robots.server.Main
import de.westermann.robots.server.util.ColorScheme
import de.westermann.robots.server.util.Configuration
import mu.KotlinLogging

/**
 * @author lars
 */
object ReplService : ThreadedService() {

    override fun start() {
        logger.info { "Start repl server on std::in..." }
        super.start()
    }

    override val logger = KotlinLogging.logger {}

    private val commands: Command = Command.create {
        command("stop", "exit", "quit", description = "Shutdown Robots server") {
            action {
                Main.stop()
            }
        }
        /*
        command("config", "configuration", description = "Print current configuration") {
            command("help") {
                action {
                    Configuration.help {
                        println(it)
                    }
                }
            }
            action {
                Configuration.log {
                    println(it)
                }
            }
        }
        */
        command("env", "environment", description = "Print current environment specs") {
            action {
                Environment.log {
                    println(it)
                }
            }
        }
        command("color", description = "Print current color scheme") {
            action {
                ColorScheme.log {
                    println(it)
                }
            }
        }
        command("robots", description = "Print all robots") {
            action {
                Printer.Table(
                        "Robots",
                        DeviceManager.robots.map {
                            Printer.Line(it.toString(), it.controllers.joinToString(", ") {
                                it.toString()
                            })
                        }
                ).log {
                    println(it)
                }
            }
        }
        command("controllers", description = "Print all controllers") {
            action {
                Printer.Table(
                        "Controllers",
                        DeviceManager.controllers.map {
                            Printer.Line(it.toString(), it.robots.joinToString(", ") {
                                it.toString()
                            })
                        }
                ).log {
                    println(it)
                }
            }
        }
        command("bind", description = "Bind a controller to a robot") {
            action {
                if (it.size != 2) {
                    throw IllegalArgumentException("$it does not match '[controllerId] [robotId]'")
                } else {
                    val controller = it[0].toIntOrNull()?.let {
                        DeviceManager.controllers[it]
                    }
                    val robot = it[1].toIntOrNull()?.let {
                        DeviceManager.robots[it]
                    }
                    if (robot != null && controller != null) {
                        DeviceManager.bindController(controller, robot)
                    }
                }
            }
        }
        command("unbind", description = "Unbind a controller from a robot") {
            action {
                if (it.size != 2) {
                    throw IllegalArgumentException("$it does not match ['controllerId' 'robotId']")
                } else {
                    val controller = it[0].toIntOrNull()?.let {
                        DeviceManager.controllers[it]
                    }
                    val robot = it[1].toIntOrNull()?.let {
                        DeviceManager.robots[it]
                    }
                    if (robot != null && controller != null) {
                        DeviceManager.unbindController(controller, robot)
                    }
                }
            }
        }
    }

    class Command private constructor(
            parent: Command?,
            private val name: List<String>,
            private val description: String?
    ) {

        private var execHandler: (List<String>) -> Unit = {
            println("Unknown command '${
            (commandName + if (it.isEmpty()) "" else " " + it.first()).trim()
            }'")
        }
        private val subCommands = mutableListOf<Command>()

        private val commandName: String = (parent?.let {
            if (it.name.isEmpty()) "" else it.name.first()
        } ?: "") + if (name.isEmpty()) "" else name.first()

        fun command(vararg name: String, description: String? = null, init: Command.() -> Unit) {
            val c = Command(this, name.toList(), description)
            c.init()
            subCommands.add(c)
        }

        fun action(exec: (List<String>) -> Unit) {
            execHandler = exec
        }

        fun exec(params: List<String>) {
            try {
                if (params.isEmpty()) {
                    execHandler(params)
                } else {
                    val name = params[0]
                    subCommands.find { name in it.name }?.exec(params.drop(1)) ?: run {
                        if (name == "help") {
                            Printer.Table((commandName + (description?.let { ": $it" } ?: "").let {
                                if (it.isEmpty()) "Robots help" else it
                            }), subCommands.map {
                                Printer.Line("${it.name.joinToString(", ")}:", it.description ?: "")
                            }).log { println(it) }
                        } else {
                            execHandler(params)
                        }
                    }
                }
            } catch (e: Exception) {
                println(e::class.simpleName + ": " + e.message)
            }
        }

        companion object {
            fun create(init: Command.() -> Unit): Command {
                val c = Command(null, emptyList(), null)
                c.init()
                return c
            }
        }
    }


    override fun run() {
        print("> ")
        val raw = readLine()
        if (raw == null) {
            println("No input available")
            logger.warn { "Repl service is not available" }
            stop()
        } else {
            if (raw.isNotBlank()) {
                commands.exec(raw.split(" +".toRegex()))
            }
        }
    }
}