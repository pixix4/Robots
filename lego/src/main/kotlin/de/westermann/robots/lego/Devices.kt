package de.westermann.robots.lego

import de.westermann.robots.lego.ev3.Ev3
import de.westermann.robots.lego.ev3.Ev3Motor
import de.westermann.robots.lego.ev3.Ev3MotorPort
import de.westermann.robots.lego.ev3.Ev3SensorPort
import de.westermann.robots.robot.device.ColorSensor
import de.westermann.robots.robot.device.GyroSensor
import de.westermann.robots.robot.device.Motor

/**
 * @author lars
 */
object Devices {
    val colorSensor: ColorSensor = Ev3.colorSensor(Ev3SensorPort.I1)
    val gyroSensor: GyroSensor = Ev3.gyroSensor(Ev3SensorPort.I2)

    val leftMotor: Motor = Ev3.motor(Ev3Motor.MotorType.LARGE, Ev3MotorPort.A)
    val rightMotor: Motor = Ev3.motor(Ev3Motor.MotorType.LARGE, Ev3MotorPort.B)
    val extraMotor: Motor = Ev3.motor(Ev3Motor.MotorType.MEDIUM, Ev3MotorPort.C)
}