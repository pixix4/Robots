package de.westermann.robots.datamodel

/**
 * @author lars
 */
interface IWebServer {
    fun bind(controllerId: Int, robotId: Int)
    fun unbind(controllerId: Int, robotId: Int)
    fun login(password: String)
    fun logout()
}