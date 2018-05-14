package de.westermann.robots.website

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.Robot
import de.westermann.robots.datamodel.observe.Library
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
    WebSocketConnection.connect {
        WebSocketConnection.iServer.login("")
    }


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
