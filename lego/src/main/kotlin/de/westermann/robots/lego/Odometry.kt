package de.westermann.robots.lego

import de.westermann.robots.robot.device.GyroSensor
import de.westermann.robots.robot.device.Motor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * @author lars
 */
object Odometry {

    var leftMotor: Motor? = null
    var rightMotor: Motor? = null
    var gyroSensor: GyroSensor? = null

    var positionX: Double = 0.0
        private set
    var positionY: Double = 0.0
        private set
    var heading: Double = 0.0
        private set

    val exec = Executors.newSingleThreadScheduledExecutor()

    data class MotorPosition(
            val left: Int,
            val right: Int
    )

    val motorPostions = mutableListOf<MotorPosition>()

    var running: Boolean = false
        private set

    fun start() {
        if (running) return
        val left = leftMotor ?: return
        val right = rightMotor ?: return
        val gyro = gyroSensor ?: return
        running = true

        var lastLeft = left.position
        var lastRight = right.position
        gyro.reset()

        exec.scheduleAtFixedRate({
            val l = left.position
            val r = right.position
            motorPostions.add(MotorPosition(l - lastLeft, r - lastRight))
            lastLeft = l
            lastRight = r
        }, 0, 50, TimeUnit.MILLISECONDS)

        exec.scheduleAtFixedRate({
            //Calc motor odometry
            //Calc gyro odometry

            motorPostions.clear()

            //Update position
        }, 0, 500, TimeUnit.MILLISECONDS)
    }

    fun stop() {
        running = false
        exec.shutdown()
    }
}