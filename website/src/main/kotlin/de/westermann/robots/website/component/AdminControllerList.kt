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
            fun reload() {
                clear()
                DeviceManager.controllers.forEach {
                    this += ControllerCard(it)
                }
            }
            DeviceManager.controllers.onChange(object : Library.Observer<Controller> {
                override fun onAdd(element: Controller) {
                    this@CardView += ControllerCard(element)
                }

                override fun onRemove(element: Controller) {
                    reload()
                }
            })
            reload()
        }
    }
}