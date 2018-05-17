package de.westermann.robots.lego.ev3

import de.westermann.robots.robot.device.UltrasonicSensor

class Ev3UltrasonicSensor(
        port: Ev3SensorPort
): UltrasonicSensor {
    private val sensor = org.ev3dev.hardware.sensors.UltrasonicSensor(port.port)

    override val distanceCentimeters: Double
        get() = sensor.distanceCentimeters.toDouble()

    override val distanceInches: Double
        get() = sensor.distanceInches.toDouble()
}