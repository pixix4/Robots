package de.westermann.robots.server.service

import io.javalin.Javalin

/**
 * @author lars
 */
object WebService {

    fun start(port: Int) {
        Javalin.create().apply {
            port(port)


            ws("/ws") {
                it.onConnect { session ->

                }
                it.onMessage { session, msg ->

                }
                it.onClose { session, _, _ ->

                }
            }
        }.start()
    }

    fun stop() {

    }
}