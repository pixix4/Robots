package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
expect object Random {
    fun int(bound: Int): Int
    fun double(): Double
    fun ints(count: Long, bound: Int): List<Int>
    fun doubles(count: Long): List<Double>
}