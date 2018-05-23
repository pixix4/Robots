package de.westermann.robots.robot.device

interface UltrasonicSensor : Sensor {
    val distanceCentimeters: Double
    val distanceInches: Double
}