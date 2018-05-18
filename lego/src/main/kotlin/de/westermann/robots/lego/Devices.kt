package de.westermann.robots.lego

import de.westermann.robots.lego.ev3.Ev3ColorSensor
import de.westermann.robots.lego.ev3.Ev3Motor
import de.westermann.robots.lego.ev3.Ev3MotorPort
import de.westermann.robots.lego.ev3.Ev3SensorPort
import de.westermann.robots.robot.device.ColorSensor

/**
 * @author lars
 */
object Devices {
    val colorSensor = Ev3ColorSensor(Ev3SensorPort.I1)
    val gyroSensor = Ev3ColorSensor(Ev3SensorPort.I1)

    val leftMotor = Ev3Motor(Ev3Motor.MotorType.LARGE, Ev3MotorPort.A)
    val rightMotor = Ev3Motor(Ev3Motor.MotorType.LARGE, Ev3MotorPort.B)
    val extraMotor = Ev3Motor(Ev3Motor.MotorType.MEDIUM, Ev3MotorPort.C)
}