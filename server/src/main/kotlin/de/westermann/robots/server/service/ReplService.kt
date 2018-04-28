package de.westermann.robots.server.service

import de.westermann.robots.server.ColorScheme
import de.westermann.robots.server.Main
import de.westermann.robots.server.utils.Configuration
import de.westermann.robots.server.utils.Environment
import de.westermann.robots.server.utils.Printer

/**
 * @author lars
 */
object ReplService : Service() {

    private val commands: Command = Command.create {
        command("stop", "exit", "quit", description = "Shutdown Robots server") {
            action {
                Main.stop()
            }
        }
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
            if (params.isEmpty()) {
                execHandler(params)
            } else {
                val name = params[0]
                subCommands.find { name in it.name }?.exec(params.drop(1)) ?: run {
                    if (name == "help") {
                        Printer.Table((commandName + (description?.let { ": $it" } ?: "").let {
                            if (it.isEmpty()) "Robots help" else it
                        }), subCommands.map {
                            Printer.Line("${it.name.joinToString(", ") }:", it.description ?: "")
                        }).log { println(it) }
                    } else {
                        execHandler(params)
                    }
                }
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
            commands.exec(raw.split(" +".toRegex()))
        }
    }
}