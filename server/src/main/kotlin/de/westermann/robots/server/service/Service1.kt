package de.westermann.robots.server.service

/**
 * @author lars
 */
interface Service1<T> : Service {
    fun start(arg: T)
}