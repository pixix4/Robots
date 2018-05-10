package de.westermann.robots.website

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.website.component.ControllerCard
import de.westermann.robots.website.component.RobotCard
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.render
import de.westermann.robots.website.toolkit.widget.cardView
import de.westermann.robots.website.toolkit.widget.navigation
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

fun main(args: Array<String>) {
    demo()

    window.onload = {
        render(document.body ?: throw IllegalStateException("Body is not available")) {
            navigation("Robots ${Date().getFullYear()}") {
                route("/", "Overview", MaterialIcon.DASHBOARD) {}
                route("/robots", "Robots", MaterialIcon.BUG_REPORT) {
                    cardView {
                        fun reload() {
                            clear()
                            DeviceManager.robots.forEach {
                                this += RobotCard(it)
                            }
                        }
                        DeviceManager.robots.onChange(object : Library.Observer<Robot> {
                            override fun onAdd(element: Robot) {
                                this@cardView += RobotCard(element)
                            }

                            override fun onRemove(element: Robot) {
                                reload()
                            }
                        })
                        reload()
                    }
                }
                route("/controllers", "Controllers", MaterialIcon.GAMEPAD) {
                    cardView {
                        fun reload() {
                            clear()
                            DeviceManager.controllers.forEach {
                                this += ControllerCard(it)
                            }
                        }
                        DeviceManager.controllers.onChange(object : Library.Observer<Controller> {
                            override fun onAdd(element: Controller) {
                                reload()
                            }

                            override fun onRemove(element: Controller) {
                                reload()
                            }
                        })
                        reload()
                    }
                }
                divider()
                route("/settings", "Settings", MaterialIcon.SETTINGS)
                route("/about", "About", MaterialIcon.INFO_OUTLINE)
            }
        }
    }
}

fun demo() {
    DeviceManager.robots += Robot(0) {
        name = "Test 1"
        color = Color.RED
    }
    DeviceManager.robots += Robot(1) {
        name = "Test 2"
    }
    DeviceManager.robots += Robot(2) {
        name = "Test 3"
    }
    DeviceManager.robots += Robot(3) {
        name = "Test 4"
        color = Color.AMBER
    }
    DeviceManager.robots += Robot(4) {
        name = "Test 5"
    }

    DeviceManager.controllers += Controller(0) {
        name = "Test 1"
    }
    DeviceManager.controllers += Controller(1) {
        name = "Test 2"
        color = Color.BLUE
    }
    DeviceManager.controllers += Controller(2) {
        name = "Test 3"
    }

    window.setTimeout({
        DeviceManager.robots[0]?.name = "Es funktioniert!"
        DeviceManager.bindController(DeviceManager.controllers[1]!!, DeviceManager.robots[1]!!)
    }, 1000)
    window.setTimeout({
        DeviceManager.bindController(DeviceManager.controllers[2]!!, DeviceManager.robots[1]!!)
    }, 2000)
    window.setTimeout({
        DeviceManager.bindController(DeviceManager.controllers[1]!!, DeviceManager.robots[2]!!)
    }, 3000)
}
