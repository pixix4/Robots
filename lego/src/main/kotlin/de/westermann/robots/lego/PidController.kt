package de.westermann.robots.lego

import kotlin.concurrent.thread
import kotlin.jvm.Volatile
import kotlin.math.max
import kotlin.math.min

/**
 * @author lars
 */
object PidController {

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
        Devices.colorSensor.let {
            foregroundRed = it.red
            foregroundGreen = it.green
            foregroundBlue = it.blue
        }
    }

    fun background() {
        Devices.colorSensor.let {
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


    const val integralLimiter: Double = 2.0 / 3.0
    const val speed: Double = 0.5
    const val countermeasure: Double = 0.2

    fun start() {
        if (running) return
        val sensor = Devices.colorSensor
        running = true

        thread(name = "pid") {
            var last_error: Double = 0.0
            var integral: Double = 0.0
            val dt: Double = 1.0

            while (running) {
                val red = (sensor.red - foregroundRed).toDouble() / (backgroundRed - foregroundRed).toDouble()
                val green = (sensor.green - foregroundGreen).toDouble() / (backgroundGreen - foregroundGreen).toDouble()
                val blue = (sensor.blue - foregroundBlue).toDouble() / (backgroundBlue - foregroundBlue).toDouble()

                val error = (max(min((red + green + blue) / 1.5, 2.0), 0.0) - 1.0) * driveMultiplier

                integral = (integral + error * dt) * integralLimiter
                val derivative = (error - last_error) / dt

                val output = constProportional * error + constIntegral * integral + constDerivative * derivative
                last_error = error

                Driver.drive((speed + (countermeasure * output)), (speed - (countermeasure * output * -1.0)))
            }

            Driver.stop()
        }
    }

    fun stop() {
        running = false
    }
}