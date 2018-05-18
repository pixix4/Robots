package de.westermann.robots.lego.ev3.demo

import de.westermann.robots.robot.device.ColorSensor

class DemoColorSensor: ColorSensor {
    override val red: Int
        get() = 0
    override val green: Int
        get() = 0
    override val blue: Int
        get() = 0
}