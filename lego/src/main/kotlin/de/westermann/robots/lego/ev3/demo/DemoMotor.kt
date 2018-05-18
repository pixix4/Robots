package de.westermann.robots.lego.ev3.demo

import de.westermann.robots.robot.device.Motor

/**
 * @author lars
 */
class DemoMotor : Motor {

    override val state: Motor.State
        get() = Motor.State.UNKNOWN

    override val dutyCycle: Int
        get() = dutyCycleSp

    override var dutyCycleSp: Int = 50

    override val position: Int
        get() = positionSp

    override var positionSp: Int = 0

    override var accelerateUpSp: Int = 0

    override var accelerateDownSp: Int = 0

    override val speed: Int
            get() = speedSp

    override var speedSp: Int = 50

    override var mode: Motor.Mode
        get() = Motor.Mode.RUN_DIRECT
        set(value) = println(value)


    override var polarity: Motor.Polarity = Motor.Polarity.NORMAL

    override var stopAction: Motor.StopAction = Motor.StopAction.BRAKE

    override fun stop() {
        println("STOP!")
    }

    override fun reset() {
    }

}