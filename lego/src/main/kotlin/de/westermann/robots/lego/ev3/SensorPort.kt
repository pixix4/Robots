package de.westermann.robots.lego.ev3

enum class SensorPort(
        val port: org.ev3dev.hardware.ports.SensorPort
) {
    I1(org.ev3dev.hardware.ports.SensorPort(org.ev3dev.hardware.ports.SensorPort.SENSOR_1)),
    I2(org.ev3dev.hardware.ports.SensorPort(org.ev3dev.hardware.ports.SensorPort.SENSOR_2)),
    I3(org.ev3dev.hardware.ports.SensorPort(org.ev3dev.hardware.ports.SensorPort.SENSOR_3)),
    I4(org.ev3dev.hardware.ports.SensorPort(org.ev3dev.hardware.ports.SensorPort.SENSOR_4))
}