package de.westermann.robots.robot.device

interface GyroSensor : Sensor {
    val angle: Int
    val rate: Int
    fun reset()
}