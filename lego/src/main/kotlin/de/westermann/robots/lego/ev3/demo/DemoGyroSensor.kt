package de.westermann.robots.lego.ev3.demo

import de.westermann.robots.robot.device.GyroSensor

class DemoGyroSensor : GyroSensor {

    override val angle: Int
        get() = 0
    override val rate: Int
        get() = 0

    override fun reset() {}

}