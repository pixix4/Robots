package de.westermann.robots.server.service

import com.studiohartman.jamepad.ControllerManager
import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.util.Button
import de.westermann.robots.datamodel.util.Track
import mu.KotlinLogging
import kotlin.math.max
import kotlin.math.min


/**
 * @author lars
 */
object GamepadService : ThreadedService() {
    override val logger = KotlinLogging.logger {}

    private var controllerManager = ControllerManager()

    private var controllers = emptyList<Controller>()

    override fun run() {
        try {
            if (controllerManager.numControllers != controllers.size) {
                controllers.forEach {
                    DeviceManager.controllers -= it
                }
                controllers = (0 until controllerManager.numControllers).map { controllerManager.getState(it) }.map {
                    Controller(DeviceManager.controllers.nextId) {
                        name = it.controllerType ?: "Unknown controller"
                        DeviceManager.controllers += this
                    }
                }

            }
            controllers.forEachIndexed { index, controller ->
                val state = controllerManager.getState(index)

                controller.iController?.onTrack(Track(
                        min(1.0, max(-1.0, state.leftStickX.toDouble())),
                        min(1.0, max(-1.0, state.leftStickY.toDouble()))
                ))
                if (state.aJustPressed || state.bJustPressed || state.xJustPressed || state.yJustPressed) {
                    controller.iController?.onButton(Button(Button.Type.A, Button.State.DOWN))
                }
                if (state.guideJustPressed || state.startJustPressed) {
                    controller.iController?.onButton(Button(Button.Type.B, Button.State.DOWN))
                }
                if (state.rb || state.dpadRight) {
                    controller.iController?.onRelativeSpeed(0.05)
                }
                if (state.lb || state.dpadLeft) {
                    controller.iController?.onRelativeSpeed(-0.05)
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
}