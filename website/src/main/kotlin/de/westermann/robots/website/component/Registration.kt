package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.observe.Library
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

        element.appendChild(TextView("") {
            classes += "registration-code"

            val observer = { newValue: String, _: String ->
                text = newValue
            }
            DeviceManager.controllers.toList().getOrNull(0)?.codeProperty?.onChangeInit(observer)
            DeviceManager.controllers.onChange(object : Library.Observer<Controller> {
                override fun onAdd(element: Controller) {
                    element.codeProperty.onChangeInit(observer)
                }

                override fun onChange(element: Controller) {
                    element.codeProperty.onChangeInit(observer)
                }
            })
        }.element)
    }
}

fun Router.registration() {
    view {
        Registration()
    }
}