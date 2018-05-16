package de.westermann.robots.website

import de.westermann.robots.website.component.*
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.condition
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.navigation
import kotlin.browser.window
import kotlin.js.Date

fun main(args: Array<String>) {
    window.onload = {
        WebSocketConnection.connect()

        Router.init {
            registration()
            route("admin") {
                condition(WebSocketConnection.adminProperty) {
                    onFalse {
                        adminLogin()
                    }
                    onTrue {
                        navigation("Robots ${Date().getFullYear()}") {
                            route("Overview", MaterialIcon.DASHBOARD) {
                                adminOverview()
                            }
                            route("robots", "Robots", MaterialIcon.BUG_REPORT) {
                                adminRobotList()
                                param(Int::class) { id ->
                                    adminRobotDetail(id)
                                }
                            }
                            route("controllers", "Controllers", MaterialIcon.GAMEPAD) {
                                adminControllerList()
                                param(Int::class) { id ->
                                    adminControllerDetail(id)
                                }
                            }
                            divider()
                            route("settings", "Settings", MaterialIcon.SETTINGS) {
                                adminSettings()
                            }
                            route("about", "About", MaterialIcon.INFO_OUTLINE) {
                                adminAbout()
                            }
                        }
                    }
                }
            }
        }
    }

    /*
    window.onload = {
        render(document.body ?: throw IllegalStateException("Body is not available")) {
            navigation("Robots ${Date().getFullYear()}") {
                entry("/", "Overview", MaterialIcon.DASHBOARD) {}
                entry("/robots", "Robots", MaterialIcon.BUG_REPORT) {
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
                entry("/controllers", "Controllers", MaterialIcon.GAMEPAD) {
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
                entry("/settings", "Settings", MaterialIcon.SETTINGS)
                entry("/about", "About", MaterialIcon.INFO_OUTLINE)
            }
        }
    }
    */
}
