package de.westermann.robots.website

import de.westermann.robots.website.component.*
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.condition
import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.navigation
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

@Suppress("UNUSED")
fun main(args: Array<String>) {
    window.onload = {
        document.getElementsByTagName("h1")[0]?.textContent = "Wait for connection..."
        WebSocketConnection.connect {
            Router.init {
                defaultController()
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
}
