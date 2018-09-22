package de.westermann.robots.datamodel

/**
 * @author lars
 */
interface IController {
    fun drive(left: Double, right: Double)
    fun kick()
    fun pid()

    fun name(name: String)
}