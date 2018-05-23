package de.westermann.robots.server.service

import de.westermann.robots.server.util.ServiceAlreadyStartedException
import de.westermann.robots.datamodel.util.toDashCase
import mu.KLogger
import kotlin.concurrent.thread
import kotlin.jvm.Volatile

/**
 * @author lars
 */
abstract class ThreadedService(
        private val daemon:Boolean = true
): Service {
    abstract val logger: KLogger;

    val name: String
        get() = this::class.simpleName?.toDashCase() ?: "robot-service"

    @Volatile
    override var running: Boolean = false

    abstract fun run()

    private var serviceThread: Thread? = null

    override fun start() {
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

    override fun stop() {
        running = false
        serviceThread?.interrupt()
    }
}