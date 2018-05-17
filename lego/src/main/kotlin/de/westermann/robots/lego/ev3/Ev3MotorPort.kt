package de.westermann.robots.lego.ev3

import org.ev3dev.hardware.ports.MotorPort

enum class Ev3MotorPort(
        val port: MotorPort
) {
    A(MotorPort(MotorPort.MOTOR_A)),
    B(MotorPort(MotorPort.MOTOR_B)),
    C(MotorPort(MotorPort.MOTOR_C)),
    D(MotorPort(MotorPort.MOTOR_D))
}