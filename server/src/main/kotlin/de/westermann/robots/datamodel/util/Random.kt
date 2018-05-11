package de.westermann.robots.datamodel.util

import kotlin.streams.toList

/**
 * @author lars
 */
actual object Random {
    val random = java.util.Random()
    actual fun int(bound: Int): Int = random.nextInt(bound)

    actual fun double(): Double = random.nextDouble()

    actual fun ints(count: Long, bound: Int): List<Int> = random.ints(count, 0, bound).toList()

    actual fun doubles(count: Long): List<Double> = random.doubles(count).toList()
}