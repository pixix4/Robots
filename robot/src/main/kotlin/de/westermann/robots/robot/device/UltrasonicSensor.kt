package de.westermann.robots.robot.device

interface UltrasonicSensor {
    val distanceCentimeters: Double
    val distanceInches: Double
}