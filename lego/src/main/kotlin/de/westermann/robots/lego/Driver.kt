package de.westermann.robots.lego

import de.westermann.robots.datamodel.util.Track
import kotlin.math.PI

/**
 * @author lars
 */
object Driver {
    private const val maxSpeed: Int = 100
    var speed: Double = 0.5
    var trim: Double = 0.0

    private const val diff = PI / 4
    private const val deg1 = PI / 2
    private const val deg2 = PI
    private const val deg3 = PI * 3 / 2

    fun drive(track: Track) {
        val angle = track.angle
        val radius = track.radius

        val (left, right) = when {
            angle < deg1 -> {
                1.0 to (angle / diff - diff)
            }
            angle < deg2 -> {
                -((angle - deg1) / diff - diff) to 1.0
            }
            angle < deg3 -> {
                -1.0 to -((angle - deg2) / diff - diff)
            }
            else -> {
                ((angle - deg1) / diff - diff) to -1.0
            }
        }
        drive(left * radius, right * radius)
    }

    fun drive(left: Double, right: Double) {
        Devices.leftMotor.dutyCycleSp = ((left * speed + trim) * maxSpeed).toInt()
        Devices.rightMotor.dutyCycleSp = ((right * speed - trim) * maxSpeed).toInt()
    }

    fun stop() {
        Devices.leftMotor.stop()
        Devices.rightMotor.stop()
    }
}