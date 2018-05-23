package de.westermann.robots.robot.device

import de.westermann.robots.datamodel.util.Color

interface ColorSensor : Sensor {
    val red: Int
    val green: Int
    val blue: Int

    val color: Color
        get() = Color(red, green, blue)
}