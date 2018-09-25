package de.westermann.robots.website.component

import de.westermann.robots.datamodel.Controller
import de.westermann.robots.datamodel.DeviceManager
import de.westermann.robots.datamodel.observe.Library
import de.westermann.robots.website.toolkit.Router
import de.westermann.robots.website.toolkit.widget.CardView

/**
 * @author lars
 */
fun Router.adminControllerList() {
    view {
        CardView<ControllerCard> {
            var controllers: Map<Controller, ControllerCard> = emptyMap()

            fun addController(controller: Controller) {
                val card = ControllerCard(controller)
                controllers += controller to card
                this += card
            }

            fun removeController(controller: Controller) {
                controllers[controller]?.let { card ->
                    this -= card
                }
            }

            DeviceManager.controllers.onChange(object : Library.Observer<Controller> {
                override fun onAdd(element: Controller) {
                    addController(element)
                }

                override fun onRemove(element: Controller) {
                    removeController(element)
                }
            })

            DeviceManager.controllers.forEach(::addController)
        }
    }
}