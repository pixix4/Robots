package de.westermann.robots.robot

/**
 * @author lars
 */

fun Int.toByteArray() = (0..3).map {
    (this.shr(it * 8).and(0xFF)).toByte()
}.toByteArray()

fun ByteArray.toDataInt() =
        foldIndexed(0) { index, acc, byte ->
            acc + byte.toInt().shl(index * 8)
        }