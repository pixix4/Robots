package de.westermann.robots.server.service

import de.westermann.robots.server.utils.ServiceAlreadyStartedException
import de.westermann.robots.server.utils.toDashCase
import mu.KotlinLogging
import kotlin.concurrent.thread

/**
 * @author lars
 */
abstract class Service(
        private val daemon:Boolean = true
) {
    protected val logger = KotlinLogging.logger {}

    val name: String
        get() = this::class.simpleName?.toDashCase() ?: "robot-service"

    @Volatile
    private var running: Boolean = false

    abstract fun run()

    private var serviceThread: Thread? = null

    open fun start() {
        if (serviceThread != null) {
            throw ServiceAlreadyStartedException("Cannot start $name twice")
        }

        running = true
        serviceThread = thread(isDaemon = daemon, name = name) {
            while (running) try {
                run()
            } catch (e: Exception) {
                logger.warn("Unexpected error in thread loop", e)
            }
        }
    }

    open fun stop() {
        running = false
        serviceThread?.interrupt()
    }
}