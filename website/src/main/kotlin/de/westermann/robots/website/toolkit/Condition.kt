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

    private var trueRoute: String? = null
    private var falseRoute: String? = null

    fun onTrue(init: Router.() -> Unit) {
        routerTrue = Router(parent).also(init)
        trueRoute = Router.currentRoute
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
        set(@Suppress("UNUSED_PARAMETER") value) {}

    override fun clear() {
        super.clear()

        routerTrue?.clear()
        routerFalse?.clear()
    }

    init {
        observable.onChange { newValue, oldValue ->
            if (oldValue) {
                trueRoute = Router.currentRoute
            } else {
                falseRoute = Router.currentRoute
            }
            updateRoute(if (newValue) trueRoute else falseRoute)
        }
    }
}

fun Router.condition(observable: ObservableProperty<Boolean>, init: Condition.() -> Unit) {
    child(Condition(this, observable)).init()
}