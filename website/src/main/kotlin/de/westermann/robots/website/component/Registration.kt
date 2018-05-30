package de.westermann.robots.website.component

import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.view.View
import de.westermann.robots.website.toolkit.widget.TextView
import kotlin.js.Date

/**
 * @author lars
 */
class Registration : View() {
    init {
        element.appendChild(TextView("Robots ${Date().getFullYear()}") {
            classes += "registration-name"
        }.element)
        element.appendChild(TextView("Wait for registration...") {
            classes += "registration-hint"
        }.element)
        element.appendChild(TextView("7395") {
            classes += "registration-code"
        }.element)
    }
}

fun Router.registration() {
    view {
        Registration()
    }
}