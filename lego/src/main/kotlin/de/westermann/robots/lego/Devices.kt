package de.westermann.robots.lego

import de.westermann.robots.datamodel.util.toDashCase
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
    fun init() {
        leftMotor.mode = Motor.Mode.RUN_DIRECT
        rightMotor.mode = Motor.Mode.RUN_DIRECT
        extraMotor.mode = Motor.Mode.RUN_DIRECT
    }

    init {
        println("Setup devices")
    }

    val leftMotor: Motor = initDevice("left") { Ev3.motor(Ev3Motor.MotorType.LARGE, Ev3MotorPort.A) }
    val rightMotor: Motor = initDevice("right") { Ev3.motor(Ev3Motor.MotorType.LARGE, Ev3MotorPort.B) }

    val extraMotor: Motor = initDevice("extra") { Ev3.motor(Ev3Motor.MotorType.MEDIUM, Ev3MotorPort.C) }

    val colorSensor: ColorSensor = initDevice { Ev3.colorSensor(Ev3SensorPort.I1) }
    val gyroSensor: GyroSensor = initDevice { Ev3.gyroSensor(Ev3SensorPort.I2) }

    private inline fun <reified T> initDevice(name: String? = null, init: () -> T): T {
        val n = (name?.let { "$it " } ?: "") + T::class.simpleName?.toDashCase()?.replace("-", " ")
        println("Init $n...")

        while (true) {
            try {
                return init()
            } catch (_: Exception) {
                println("Please connect the device correctly!")
            }
        }
    }
}