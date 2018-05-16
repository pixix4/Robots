package de.westermann.robots.robot.device

interface GyroSensor {
    val angle: Int
    val rate: Int
    fun reset()
}