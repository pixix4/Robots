package de.westermann.robots.robot.device

/**
 * @author lars
 */
interface Motor {
    val state: State

    val dutyCycle: Int
    var dutyCycleSp: Int

    val position: Int
    var positionSp: Int

    var accelerateUpSp: Int
    var accelerateDownSp: Int

    val speed: Int
    var speedSp: Int

    var mode: Mode
    var polarity: Polarity
    var stopAction: StopAction

    fun stop()
    fun reset()

    enum class Mode {
        RUN_DIRECT, RUN_RELATIVE, RUN_ABSOLUTE, RUN_TIMED
    }

    enum class Polarity {
        NORMAL, INVERSE
    }

    enum class State {

        /**
         * The motor is not turning, but rather attempting to hold a fixed position.
         */
        HOLDING,
        
        /**
         * The motor is turning, but cannot reach its speed_sp.
         */
        OVERLOADED,
        
        /**
         * The motor is accelerating up or down and has not yet reached a constant output level.
         */
        ACCELERATING,
        
        /**
         * Power is being sent to the motor.
         */
        RUNNING,
        
        /**
         * The motor is not turning when it should be.
         */
        STALLED,

        /**
         * The state of the motor cannot be determined
         */
        UNKNOWN
    }

    enum class StopAction {

        /**
         * Power will be removed from the motor and a passive electrical load will be placed on the motor. This is usually done by shorting the motor terminals together. This load will absorb the energy from the rotation of the track and cause the motor to stop more quickly than coasting.
         */
        BRAKE,

        /**
         * Power will be removed from the motor and it will freely coast to a stop.
         */
        COAST,

        /**
         * Does not remove power from the motor. Instead it actively try to hold the motor at the current position. If an external force tries to turn the motor, the motor will push back to maintain its position.
         */
        HOLD

        
    }
}