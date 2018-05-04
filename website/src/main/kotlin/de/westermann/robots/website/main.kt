package de.westermann.robots.website

import de.westermann.robots.website.toolkit.icon.MaterialIcon
import de.westermann.robots.website.toolkit.render
import de.westermann.robots.website.toolkit.widget.navigation
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

fun main(args: Array<String>) {
    window.onload = {
        render(document.body ?: throw IllegalStateException("Body is not available")) {
            navigation("Robots ${Date().getFullYear()}") {
                route("", "Overview", MaterialIcon.DASHBOARD) {

                }
                route("robots", "Robots", MaterialIcon.BUG_REPORT) {}
                route("controllers", "Controllers", MaterialIcon.GAMEPAD) {}
                divider()
                route("settings", "Settings", MaterialIcon.SETTINGS)
                route("about", "About", MaterialIcon.INFO_OUTLINE)
            }
        }
    }
}
