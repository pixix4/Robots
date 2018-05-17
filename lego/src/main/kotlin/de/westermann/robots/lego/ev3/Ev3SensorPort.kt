package de.westermann.robots.lego.ev3

import org.ev3dev.hardware.ports.SensorPort

enum class Ev3SensorPort(
        val port: SensorPort
) {
    I1(SensorPort(SensorPort.SENSOR_1)),
    I2(SensorPort(SensorPort.SENSOR_2)),
    I3(SensorPort(SensorPort.SENSOR_3)),
    I4(SensorPort(SensorPort.SENSOR_4))
}