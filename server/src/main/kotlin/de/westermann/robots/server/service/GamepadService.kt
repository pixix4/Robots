package de.westermann.robots.server.service

import com.studiohartman.jamepad.ControllerManager
import com.studiohartman.jamepad.ControllerState
import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.util.Track
import de.westermann.robots.server.util.Configuration
import mu.KotlinLogging
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min


/**
 * @author lars
 */
object GamepadService : ThreadedService() {
    override val logger = KotlinLogging.logger {}

    private val maxControllerCount = Configuration.General.maxPhysicalControllerCount

    private var controllerManager = ControllerManager(maxControllerCount)

    private var controllers: Map<Int, Gamepad> = emptyMap()

    override fun run() {
        try {
            (0 until maxControllerCount).forEach { index ->
                val state = controllerManager.getState(index)

                if (state.isConnected) {
                    val gamepad = controllers.getOrElse(index) {
                        // Add controller

                        val g = Gamepad(Controller(DeviceManager.controllers.nextId) {
                            name = state.controllerType ?: "Unknown controller"
                            type = Controller.Type.PHYSICAL
                            DeviceManager.controllers += this
                        })

                        controllers += index to g
                        g
                    }

                    gamepad.adapter.perform(state)


                } else {
                    controllers[index]?.let { gamepad ->
                        // Remove controller

                        DeviceManager.controllers -= gamepad.controller
                        controllers -= index
                    }
                }
            }

            Thread.sleep(100)
        } catch (_: IllegalStateException) {
        } catch (_: NullPointerException) {
        } catch (_: InterruptedException) {
        }
    }

    override fun start() {
        super.start()
        controllerManager.initSDLGamepad()
    }

    override fun stop() {
        super.stop()
        controllerManager.quitSDLGamepad()
    }

    class Gamepad(
            val controller: Controller,
            var adapter: GamepadAdapter = GamepadAdapter.RobertsAdapter(controller)
    )

    sealed class GamepadAdapter(
            val controller: Controller
    ) {
        abstract fun perform(state: ControllerState);

        companion object {
            val DIFF = PI * 0.25
            val DEG1 = PI * 0.5
            val DEG2 = PI
            val DEG3 = PI * 1.5
        }

        class RobertsAdapter(controller: Controller) : GamepadAdapter(controller) {

            var leftTriggerMin = 0.2
            var leftTriggerMax = 0.2
            var rightTriggerMin = 0.8
            var rightTriggerMax = 0.8

            override fun perform(state: ControllerState) {
                controller.iController?.let { sender ->

                    if (state.aJustPressed || state.bJustPressed || state.xJustPressed || state.yJustPressed) {
                        sender.kick()
                    }

                    val leftTrigger = state.leftTrigger.toDouble()
                    if (leftTrigger < leftTriggerMin) {
                        leftTriggerMin = leftTrigger
                    }
                    if (leftTrigger > leftTriggerMax) {
                        leftTriggerMax = leftTrigger
                    }
                    val left = (leftTrigger-leftTriggerMin) * leftTriggerMax

                    val rightTrigger = state.rightTrigger.toDouble()
                    if (rightTrigger < rightTriggerMin) {
                        rightTriggerMin = rightTrigger
                    }
                    if (rightTrigger > rightTriggerMax) {
                        rightTriggerMax = rightTrigger
                    }
                    val right = (rightTrigger-rightTriggerMin) * rightTriggerMax

                    val accelerate = min(1.0, max(0.0, right))
                    val decelerate = min(1.0, max(0.0, left))

                    val stickX = min(1.0, max(-1.0, state.leftStickX.toDouble()))
                    val stickY = min(1.0, max(-1.0, state.leftStickY.toDouble()))

                    if (state.lb && state.rb) {
                        val speed = (accelerate - decelerate) / 2 + 0.5

                        val track = Track(stickX, stickY)

                        val angle = track.angle
                        val radius = track.radius * speed

                        when {
                            radius == 0.0 -> {
                                sender.drive(0.0, 0.0)
                            }
                            angle < DEG1 -> {
                                sender.drive(
                                        radius,
                                        ((angle - DIFF) / DIFF) * radius
                                )
                            }
                            angle < DEG2 -> {
                                sender.drive(
                                        -((angle - DEG1 - DIFF) / DIFF) * radius,
                                        radius
                                )
                            }
                            angle < DEG3 -> {
                                sender.drive(
                                        -radius,
                                        -((angle - DEG2 - DIFF) / DIFF) * radius
                                )
                            }
                            else -> {
                                sender.drive(
                                        ((angle - DEG3 - DIFF) / DIFF) * radius,
                                        -radius
                                )
                            }
                        }
                    } else {
                        if ((accelerate > 0.9 && decelerate > 0.9) || (state.rb xor state.lb)) {
                            val track = Track(stickX, stickY)
                            val speed = track.radius * if (accelerate - decelerate < 0) -1.0 else 1.0

                            if (stickX < 0.0) {
                                sender.drive(-speed, speed)
                            } else if (stickX > 0.0) {
                                sender.drive(speed, -speed)
                            } else {
                                sender.drive(0.0, 0.0)
                            }

                        } else {

                            val speed = accelerate - decelerate

                            var left1 = speed
                            var right1 = speed

                            if (stickX < 0.0) {
                                left1 *= 1.0 + stickX
                            } else if (stickX > 0.0) {
                                right1 *= 1.0 - stickX
                            }

                            sender.drive(left1, right1)
                        }
                    }
                }
            }
        }

        /*
        class DefaultAdapter(controller: Controller) : GamepadAdapter(controller) {

            private var speed = 0.5

            override fun perform(state: ControllerState) {
                controller.iController?.let { sender ->

                    if (state.aJustPressed || state.bJustPressed || state.xJustPressed || state.yJustPressed) {
                        sender.kick()
                    }
                    if (state.guideJustPressed || state.startJustPressed) {
                        sender.pid()
                    }
                    if (state.rb || state.dpadRight) {
                        speed = min(speed + 0.05, 1.0)
                    }
                    if (state.lb || state.dpadLeft) {
                        speed = max(speed - 0.05, 0.0)
                    }

                    val track = Track(
                            min(1.0, max(-1.0, state.leftStickX.toDouble())),
                            min(1.0, max(-1.0, state.leftStickY.toDouble()))
                    )

                    val angle = track.angle
                    val radius = track.radius * speed

                    when {
                        radius == 0.0 -> {
                            sender.drive(0.0, 0.0)
                        }
                        angle < DEG1 -> {
                            sender.drive(
                                    radius,
                                    ((angle - DIFF) / DIFF) * radius
                            )
                        }
                        angle < DEG2 -> {
                            sender.drive(
                                    -((angle - DEG1 - DIFF) / DIFF) * radius,
                                    radius
                            )
                        }
                        angle < DEG3 -> {
                            sender.drive(
                                    -radius,
                                    -((angle - DEG2 - DIFF) / DIFF) * radius
                            )
                        }
                        else -> {
                            sender.drive(
                                    ((angle - DEG3 - DIFF) / DIFF) * radius,
                                    -radius
                            )
                        }
                    }
                }
            }
        }
        */
    }
}