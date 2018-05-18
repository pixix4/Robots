package de.westermann.robots.lego.ev3.demo

import de.westermann.robots.robot.device.UltrasonicSensor

class DemoUltrasonicSensor: UltrasonicSensor {

    override val distanceCentimeters: Double
        get() = 2.54

    override val distanceInches: Double
        get() = 1.0
}