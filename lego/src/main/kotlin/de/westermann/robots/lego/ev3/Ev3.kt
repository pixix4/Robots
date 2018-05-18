package de.westermann.robots.lego.ev3

import de.westermann.robots.lego.ev3.demo.*
import de.westermann.robots.robot.device.ColorSensor
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author lars
 */
object Ev3 {
    val demo: Boolean = Files.notExists(Paths.get("/sys/class/lego-port"))

    fun colorSensor(port: Ev3SensorPort): ColorSensor =
            if (demo) {
                DemoColorSensor()
            } else {
                Ev3ColorSensor(port)
            }

    fun gyroSensor(port: Ev3SensorPort) =
            if (demo) {
                DemoGyroSensor()
            } else {
                Ev3GyroSensor(port)
            }

    fun touchSensor(port: Ev3SensorPort) =
            if (demo) {
                DemoTouchSensor()
            } else {
                Ev3TouchSensor(port)
            }

    fun ultrasonicSensor(port: Ev3SensorPort) =
            if (demo) {
                DemoUltrasonicSensor()
            } else {
                Ev3UltrasonicSensor(port)
            }

    fun motor(type: Ev3Motor.MotorType, port: Ev3MotorPort) =
            if (demo) {
                DemoMotor()
            } else {
                Ev3Motor(type, port)
            }
}