package de.westermann.robots.server

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.util.*
import de.westermann.robots.server.util.Charsets
import de.westermann.robots.server.util.Configuration

/**
 * @author lars
 */

object Demo {
    fun load() {
        DeviceManager.robots += Robot(0) {
            name = "Demo robot 1"
            version = Version(0, 4, 3)
            kicker = Kicker(true)
            type = "Demobot"
            energy = Energy(0.8, Energy.State.DISCHARGING)
        }
        DeviceManager.robots += Robot(1) {
            name = "Demo robot 2"
            kicker = Kicker(true)
            camera = Camera(true)
            type = "Demobot"
            energy = Energy(0.4, Energy.State.CHARGING)
        }
        DeviceManager.robots += Robot(2) {
            name = "Demo robot 3"
            type = "Demobot"
            color = Color.AMBER
            energy = Energy(0.0, Energy.State.NO_BATTERY)
        }
        DeviceManager.robots += Robot(3) {
            name = "Demo robot 4"
            type = "Demobot"
            kicker = Kicker(true)
            color = Color.RED
            energy = Energy(0.5, Energy.State.UNKNOWN)
        }

        DeviceManager.controllers += Controller(0) {
            name = "Demo controller 1"
            generateCode(
                    Configuration.properties.controllerCodeLength,
                    Charsets.charsetsToList(Configuration.properties.controllerCodeCharset).toList()
            )
            type = Controller.Type.DESKTOP
        }
        DeviceManager.controllers += Controller(1) {
            name = "Demo controller 2"
            generateCode(
                    Configuration.properties.controllerCodeLength,
                    Charsets.charsetsToList(Configuration.properties.controllerCodeCharset).toList()
            )
            description = "A random browser identifier"
            type = Controller.Type.MOBIL
        }
        DeviceManager.controllers += Controller(2) {
            name = "Demo controller 3"
            type = Controller.Type.PHYSICAL
            color = Color.BLUE_GREY
        }
        DeviceManager.controllers += Controller(3) {
            name = "Demo controller 4"
            type = Controller.Type.UNKNOWN
        }

        bind(DeviceManager.controllers[1], DeviceManager.robots[0])
        bind(DeviceManager.controllers[3], DeviceManager.robots[0])
        bind(DeviceManager.controllers[2], DeviceManager.robots[3])
    }

    private fun bind(controller: Controller?, robot: Robot?) {
        if (robot != null && controller != null) {
            DeviceManager.bindController(controller, robot)
        }
    }
}