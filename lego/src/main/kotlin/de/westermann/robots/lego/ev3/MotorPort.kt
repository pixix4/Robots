package de.westermann.robots.lego.ev3

enum class MotorPort(
        val port: org.ev3dev.hardware.ports.MotorPort
) {
    A(org.ev3dev.hardware.ports.MotorPort(org.ev3dev.hardware.ports.MotorPort.MOTOR_A)),
    B(org.ev3dev.hardware.ports.MotorPort(org.ev3dev.hardware.ports.MotorPort.MOTOR_B)),
    C(org.ev3dev.hardware.ports.MotorPort(org.ev3dev.hardware.ports.MotorPort.MOTOR_C)),
    D(org.ev3dev.hardware.ports.MotorPort(org.ev3dev.hardware.ports.MotorPort.MOTOR_D))
}