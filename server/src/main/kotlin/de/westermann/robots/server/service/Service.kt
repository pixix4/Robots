package de.westermann.robots.server.service

/**
 * @author lars
 */
interface Service {
    val running: Boolean
    fun start()
    fun stop()
}