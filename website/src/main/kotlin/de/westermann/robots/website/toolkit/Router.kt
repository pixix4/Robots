package de.westermann.robots.website.toolkit

import de.westermann.robots.website.toolkit.view.View
import org.w3c.dom.HTMLElement
import org.w3c.dom.PopStateEvent
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.clear
import kotlin.reflect.KClass

/**
 * @author lars
 */
open class Router(
        val parent: Router? = null,
        private val path: String = ""
) {
    private var routes = mapOf<String, Router>()
    protected open var forward = emptyList<Router>()
    private var param: ((args: List<String>) -> List<String>?)? = null

    fun route(route: String, exec: Router.() -> Unit) {
        routes += route to Router(this, route).also(exec)
    }

    fun <T : Any> param(type: KClass<T>, exec: Router.(param: T) -> Unit) {
        var map = emptyMap<T, Router>()
        param = { args ->
            if (args.count() != 1)
                null
            else {
                @Suppress("UNCHECKED_CAST")
                (when (type) {
                    Int::class -> args.first().toIntOrNull()
                    String::class -> args.first()
                    else -> null
                } as? T?)?.let { arg ->
                    if (!map.containsKey(arg)) {
                        map += arg to Router(this, arg.toString()).also { it.exec(arg) }
                    }
                    map[arg]?.render()
                    listOf(arg.toString())
                }
            }
        }
    }

    private fun perform(path: List<String>): List<String>? {
        forward.forEach {
            it.perform(path)?.let {
                return listOf(this.path) + it
            }
        }

        val first = path.getOrNull(0) ?: ""
        routes[first]?.let {
            it.perform(path.drop(1))?.let {
                return listOf(this.path) + it
            }
        }

        param?.let {
            it(path)?.let {
                return listOf(this.path) + it
            }
        }

        routes[""]?.let {
            it.perform(path.drop(1))?.let {
                return listOf(this.path) + it
            }
        }

        return if (displayable) {
            render()
            listOf(this.path)
        } else {
            null
        }
    }

    protected open val rootView: HTMLElement?
        get() = if (parent == null) document.body else parent.rootView

    private var viewCreators = emptyList<() -> View>()
    private var views = emptyList<View>()
        get() {
            viewCreators.forEach {
                field += it()
            }
            viewCreators = emptyList()
            return field
        }

    private val displayable: Boolean
        get() = views.isNotEmpty()

    private val nextRenderer: Router?
        get() = parent?.let {
            if (rootView != it.rootView) this else it.nextRenderer
        }

    private var renderListener = emptyList<(top: Boolean) -> Unit>()
    fun onRender(func: (top: Boolean) -> Unit) {
        renderListener += func
    }

    private fun bubbleRender(top: Boolean) {
        parent?.bubbleRender(false)
        renderListener.forEach { it(top) }
    }

    private fun render(top: Boolean = true) {
        parent?.nextRenderer?.render(false)
        if (top) {
            bubbleRender(top)
        }
        (parent?.rootView ?: rootView)?.let { root ->
            root.clear()
            views.forEach {
                root.appendChild(it.element)
            }
        }
    }

    val fullPath: String
        get() = ((if (parent == null) "" else parent.fullPath + "/") + path)
                .replace("/+".toRegex(), "/")
                .dropLastWhile { it == '/' }

    fun <T : Router> child(router: T): T {
        forward += router
        return router
    }

    fun <T : View> view(init: () -> T) {
        viewCreators += init
    }

    companion object {
        private val root = Router()

        fun init(init: Router.() -> Unit) {
            root.init()
            routeTo()

            window.onpopstate = {
                (it as? PopStateEvent)?.let {
                    routeTo(window.location.pathname)
                }
            }
        }

        fun routeTo(route: String? = null) {
            val toPos = route ?: window.location.pathname
            root.perform(toPos.split("/").filter(String::isNotBlank))?.let {
                val url = "/"+it.filter(String::isNotBlank).joinToString("/")
                if (route == null) {
                    window.history.replaceState(null, "", url)
                } else {
                    window.history.pushState(null, "", url)
                }
            } ?: println("Error")
        }
    }
}