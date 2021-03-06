package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
actual object Random {
    actual fun int(bound: Int): Int = (double() * bound).toInt()

    @Suppress("DEPRECATION")
    actual fun double(): Double = kotlin.js.Math.random()

    actual fun ints(count: Long, bound: Int): List<Int> = (1..count).map { int(bound) }

    actual fun doubles(count: Long): List<Double> = (1..count).map { double() }
}