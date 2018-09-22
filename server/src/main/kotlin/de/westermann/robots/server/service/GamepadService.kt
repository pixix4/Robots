package de.westermann.robots.server.service

import com.studiohartman.jamepad.ControllerManager
import com.studiohartman.jamepad.ControllerState
import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.Track
import de.westermann.robots.server.util.Configuration
import mu.KotlinLogging
import java.util.*
import kotlin.concurrent.schedule
import kotlin.math.PI
import kotlin.math.abs
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

        DeviceManager.onBindChange(object : DeviceManager.OnBindChange {
            override fun onBind(controller: Controller, robot: Robot) {
                logger.info { "bind ${controllers.size}" }
                controllers.filterValues { it.controller == controller }.keys.forEach { index ->
                    logger.info { "vibrate $index!" }
                    logger.info { controllerManager.startVibration(index, 1.0.toFloat(), 1.0.toFloat()); }

                    Timer().schedule(500) {
                        logger.info { "vibrate stop!" }
                        controllerManager.stopVibration(index)
                    }
                }
            }

            override fun onUnbind(controller: Controller, robot: Robot) {
                controllers.filterValues { it.controller == controller }.keys.forEach { index ->
                    controllerManager.startVibration(index, 1.0.toFloat(), 1.0.toFloat());
                    Timer().schedule(500) {
                        controllerManager.stopVibration(index)
                    }
                }
            }

        })

    }

    override fun stop() {
        super.stop()
        controllerManager.quitSDLGamepad()
    }

    class Gamepad(
            val controller: Controller,
            var adapter: GamepadAdapter = GamepadAdapter.MarioKart(controller)
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

        class MarioKart(controller: Controller) : GamepadAdapter(controller) {
            override fun perform(state: ControllerState) {
                controller.iController?.let { sender ->
                    val accelerate = min(1.0, max(0.0, state.rightTrigger.toDouble()))
                    val decelerate = min(1.0, max(0.0, state.leftTrigger.toDouble()))

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

                            var left = speed
                            var right = speed

                            if (stickX < 0.0) {
                                left *= 1.0 + stickX
                            } else if (stickX > 0.0) {
                                right *= 1.0 - stickX
                            }

                            sender.drive(left, right)
                        }
                    }
                }
            }
        }

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
    }
}