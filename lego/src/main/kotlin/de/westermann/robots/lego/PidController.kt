package de.westermann.robots.lego

import de.westermann.robots.robot.device.ColorSensor
import de.westermann.robots.robot.device.Motor
import kotlin.concurrent.thread
import kotlin.math.max
import kotlin.math.min

/**
 * @author lars
 */
object PidController {

    var colorSensor: ColorSensor? = null
    var leftMotor: Motor? = null
    var rightMotor: Motor? = null

    @Volatile
    var foregroundRed: Int = 0

    @Volatile
    var foregroundGreen: Int = 0

    @Volatile
    var foregroundBlue: Int = 0


    @Volatile
    var backgroundRed: Int = 255

    @Volatile
    var backgroundGreen: Int = 255

    @Volatile
    var backgroundBlue: Int = 255

    var driveRight: Boolean
        get() = driveMultiplier == 1.0
        set(value) {
            driveMultiplier = if (value) 1.0 else -1.0
        }

    @Volatile
    private var driveMultiplier: Double = 1.0


    fun foreground(red: Int, green: Int, blue: Int) {
        foregroundRed = red
        foregroundGreen = green
        foregroundBlue = blue
    }

    fun background(red: Int, green: Int, blue: Int) {
        backgroundRed = red
        backgroundGreen = green
        backgroundBlue = blue
    }


    fun foreground() {
        colorSensor?.let {
            foregroundRed = it.red
            foregroundGreen = it.green
            foregroundBlue = it.blue
        }
    }

    fun background() {
        colorSensor?.let {
            backgroundRed = it.red
            backgroundGreen = it.green
            backgroundBlue = it.blue
        }
    }

    @Volatile
    var running: Boolean = false

    const val constProportional: Double = 1.0
    const val constIntegral: Double = 1.0
    const val constDerivative: Double = 1.0

    @Volatile
    var speed: Double = 50.0
    @Volatile
    var countermeasure: Double = 20.0

    const val integralLimiter: Double = 2.0 / 3.0

    fun start() {
        if (running) return
        val sensor = colorSensor ?: return
        val left = leftMotor ?: return
        val right = rightMotor ?: return
        running = true

        thread(name = "pid") {
            var last_error: Double = 0.0
            var integral: Double = 0.0
            val dt: Double = 1.0

            var leftPos = left.position
            var rightPos = right.position

            while (running) {
                val red = (sensor.red - foregroundRed).toDouble() / (backgroundRed - foregroundRed).toDouble()
                val green = (sensor.green - foregroundGreen).toDouble() / (backgroundGreen - foregroundGreen).toDouble()
                val blue = (sensor.blue - foregroundBlue).toDouble() / (backgroundBlue - foregroundBlue).toDouble()

                val error = (max(min((red + green + blue) / 1.5, 2.0), 0.0) - 1.0) * driveMultiplier

                integral = (integral + error * dt) * integralLimiter
                val derivative = (error - last_error) / dt

                val output = constProportional * error + constIntegral * integral + constDerivative * derivative
                last_error = error

                left.dutyCycleSp = (speed + (countermeasure * error)).toInt()
                right.dutyCycleSp = (speed - (countermeasure * error * -1.0)).toInt()
            }

            left.stop()
            right.stop()
        }
    }

    fun stop() {
        running = false
    }
}