package de.westermann.robots.lego.ev3

import de.westermann.robots.robot.device.ColorSensor

class Ev3ColorSensor(
        port: Ev3SensorPort
) : ColorSensor {
    private val sensor = org.ev3dev.hardware.sensors.ColorSensor(port.port).also {
        it.mode = org.ev3dev.hardware.sensors.ColorSensor.SYSFS_RGB_MODE
    }

    override val red: Int
        get() = (sensor.rgB_Red / 4.7).toInt()
    override val green: Int
        get() = (sensor.rgB_Green / 4.7).toInt()
    override val blue: Int
        get() = (sensor.rgB_Blue / 4.7).toInt()
}