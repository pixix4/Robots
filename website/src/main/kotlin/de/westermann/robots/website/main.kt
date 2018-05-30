package de.westermann.robots.website

import de.westermann.robots.website.component.*
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.condition
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.navigation
import de.westermann.robots.website.toolkit.widget.box
import de.westermann.robots.website.toolkit.widget.textView
import kotlin.browser.window
import kotlin.js.Date

@Suppress("UNUSED")
fun main(args: Array<String>) {
    Router.stopRouting()
    window.onunload = {
        Router.stopRouting()
    }
    window.onload = {
        Router.init {
            condition(WebSocketConnection.connectedProperty) {
                onFalse {
                    box {
                        classes += "wait-for-connection"
                        textView("Wait for connection...")
                    }
                }
                onTrue {
                    condition(WebSocketConnection.registeredProperty) {
                        onFalse {
                            registration()
                        }
                        onTrue {
                            defaultController()
                        }
                    }
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
        }
        WebSocketConnection.connect()
    }
}
