package de.westermann.robots.lego.ev3

import de.westermann.robots.robot.device.TouchSensor

class Ev3TouchSensor(
        port: Ev3SensorPort
) : TouchSensor {
    private val sensor = org.ev3dev.hardware.sensors.TouchSensor(port.port)

    override val isPressed: Boolean
        get() = sensor.isPressed
}