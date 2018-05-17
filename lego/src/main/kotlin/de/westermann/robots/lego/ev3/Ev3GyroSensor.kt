package de.westermann.robots.lego.ev3

import de.westermann.robots.robot.device.GyroSensor

class Ev3GyroSensor(
        port: Ev3SensorPort
) : GyroSensor {
    private val sensor = org.ev3dev.hardware.sensors.GyroSensor(port.port)

    override val angle: Int
        get() = sensor.angle
    override val rate: Int
        get() = sensor.rate

    override fun reset() {
        sensor.sendCommand(org.ev3dev.hardware.sensors.GyroSensor.SYSFS_ANGLE_MODE)
    }

}