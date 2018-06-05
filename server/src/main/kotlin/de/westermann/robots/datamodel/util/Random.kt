package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
actual object Random {
    val random = java.util.Random()
    actual fun int(bound: Int): Int = random.nextInt(bound)

    actual fun double(): Double = random.nextDouble()

    actual fun ints(count: Long, bound: Int): List<Int> = random.ints(count, 0, bound).toArray().toList()

    actual fun doubles(count: Long): List<Double> = random.doubles(count).toArray().toList()
}