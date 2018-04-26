package de.westermann.robots.lego.ev3

import de.westermann.robots.robot.device.Motor
import org.ev3dev.hardware.motors.LargeMotor
import org.ev3dev.hardware.motors.MediumMotor
import org.ev3dev.hardware.ports.MotorPort

/**
 * @author lars
 */
class Ev3Motor(
        type: MotorType,
        port: MotorPort
) : Motor {

    private val motor: org.ev3dev.hardware.motors.Motor = when (type) {
        Ev3Motor.MotorType.LARGE -> LargeMotor(port)
        Ev3Motor.MotorType.MEDIUM -> MediumMotor(port)
    }

    enum class MotorType {
        LARGE, MEDIUM
    }

    override val state: Motor.State
        get() = when (motor.stateViaString) {
            "running" -> Motor.State.RUNNING
            "stalled" -> Motor.State.STALLED
            "ramping" -> Motor.State.ACCELERATING
            "holding" -> Motor.State.HOLDING
            "overloaded" -> Motor.State.OVERLOADED
            else -> Motor.State.UNKNOWN
        }

    override val dutyCycle: Int
        get() = motor.dutyCycle

    override var dutyCycleSp: Int
        get() = motor.dutyCycleSP
        set(value) {
            motor.dutyCycleSP = value
        }

    override val position: Int
        get() = motor.position

    override var positionSp: Int
        get() = motor.position_SP
        set(value) {
            motor.position_SP = value
        }

    override var accelerateUpSp: Int
        get() = motor.ramp_Up_SP
        set(value) {
            motor.ramp_Up_SP = value
        }

    override var accelerateDownSp: Int
        get() = motor.ramp_Down_SP
        set(value) {
            motor.ramp_Down_SP = value
        }

    override val speed: Int
        get() = motor.speed

    override var speedSp: Int
        get() = motor.speed_SP
        set(value) {
            motor.speed_SP = value
        }

    override var mode: Motor.Mode
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
        set(value) {}

    override var polarity: Motor.Polarity
        get() = when (motor.polarity) {
            "normal" -> Motor.Polarity.NORMAL
            "inversed" -> Motor.Polarity.INVERSE
            else -> throw IllegalStateException("Illegal polarity '${motor.polarity}'")
        }
        set(value) {
            motor.polarity = when (value) {
                Motor.Polarity.NORMAL -> "normal"
                Motor.Polarity.INVERSE -> "inversed"
            }
        }

    override var stopAction: Motor.StopAction
        get() = when (motor.stopAction) {
            "coast" -> Motor.StopAction.COAST
            "brake" -> Motor.StopAction.BRAKE
            "hold" -> Motor.StopAction.HOLD
            else -> throw IllegalStateException("Illegal stop action '${motor.stopAction}'")
        }
        set(value) {
            motor.polarity = when (value) {
                Motor.StopAction.BRAKE -> "brake"
                Motor.StopAction.COAST -> "coast"
                Motor.StopAction.HOLD -> "hold"
            }
        }

    override fun stop() {
        motor.stop()
    }

    override fun reset() {
        motor.reset()
    }

}