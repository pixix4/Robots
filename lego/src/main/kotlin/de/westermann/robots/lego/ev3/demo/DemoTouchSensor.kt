package de.westermann.robots.lego.ev3.demo

import de.westermann.robots.robot.device.TouchSensor

class DemoTouchSensor: TouchSensor {

    override val isPressed: Boolean
        get() = false
}