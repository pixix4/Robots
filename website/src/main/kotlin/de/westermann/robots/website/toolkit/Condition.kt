package de.westermann.robots.website.toolkit

import de.westermann.robots.datamodel.observe.ObservableProperty

/**
 * @author lars
 */
class Condition(
        parent: Router,
        private val observable: ObservableProperty<Boolean>
) : Router(parent) {
    private var routerTrue: Router? = null
    private var routerFalse: Router? = null

    fun onTrue(init: Router.() -> Unit) {
        routerTrue = Router(parent).also(init)
    }

    fun onFalse(init: Router.() -> Unit) {
        routerFalse = Router(parent).also(init)
    }

    override var forward: List<Router>
        get() = if (observable.value) {
            listOf(routerTrue)
        } else {
            listOf(routerFalse)
        }.filterNotNull()
        set(value) {}

    init {
        observable.onChange { _, _ ->
            updateRoute()
        }
    }
}

fun Router.condition(observable: ObservableProperty<Boolean>, init: Condition.() -> Unit) {
    child(Condition(this, observable)).init()
}