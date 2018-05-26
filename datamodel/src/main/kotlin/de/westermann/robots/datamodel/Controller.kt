package de.westermann.robots.datamodel

import de.westermann.robots.datamodel.observe.ObservableObject
import de.westermann.robots.datamodel.observe.accessor
import de.westermann.robots.datamodel.search.StringSimilarity
import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.Json
import de.westermann.robots.datamodel.util.Random
import de.westermann.robots.datamodel.util.json

/**
 * @author lars
 */
class Controller(
        override val id: Int
) : ObservableObject() {

    constructor(id: Int, init: Controller.() -> Unit) : this(id) {
        init(this)
    }

    constructor(init: Controller.() -> Unit) : this(DeviceManager.controllers.nextId) {
        init(this)
    }

    val nameProperty = "".observable()
    var name by nameProperty.accessor()

    val codeProperty = "".observable()
    var code by codeProperty.accessor()

    val typeProperty = Type.UNKNOWN.observable()
    var type by typeProperty.accessor()

    val descriptionProperty = "".observable()
    var description by descriptionProperty.accessor()

    val colorProperty = Color.TRANSPARENT.observable()
    var color by colorProperty.accessor()

    var iController: IController? = null

    fun generateCode(length: Int, chars: List<Char>) {
        fun gen() = Random.ints(length.toLong(), chars.size).map { chars[it] }.joinToString("")

        val codes = DeviceManager.controllers.map { it.code }.toString()

        var h: String
        do {
            h = gen()
        } while (h in codes)
        code = h
    }

    val robotsProperty = {
        DeviceManager.getBoundRobots(this)
    }.observableFunction()
    val robots by robotsProperty.accessor()

    enum class Type {
        DESKTOP, MOBIL, PHYSICAL, UNKNOWN;

        companion object {

            private val mobilIndicator = listOf("mobil", "iphone", "ipad", "ipod", "android", "phone")
            private val desktopIndicator = listOf("linux", "imac", "macbook", "windows")

            fun detect(name: String): Type {
                if (mobilIndicator.any { name.contains(it, true) }) return MOBIL
                if (desktopIndicator.any { name.contains(it, true) }) return DESKTOP
                return UNKNOWN
            }
        }
    }

    override fun toString(): String = "Controller($id: '$name')"

    override fun toJson(): Json = json {
        value("id") { id }
        value("name") { name }
        value("code") { code }
        value("type") { type.name }
        value("description") { description }
        value("color") { color.toString() }
    }

    override fun fromJson(json: Json) {
        json["name"]?.toString()?.let { name = it }
        json["code"]?.toString()?.let { code = it }
        json["type"]?.toString()?.let { t ->
            Type.values().find { it.name.equals(t, ignoreCase = true) }?.let {
                type = it
            }
        }
        json["description"]?.toString()?.let { description = it }
        json["color"]?.toString()?.let {
            try {
                color = Color.parse(it)
            } catch (_: IllegalArgumentException) {
            }
        }
    }

    override fun probability(search: String): Double = StringSimilarity.check(
            search,
            name to 1.0,
            type.name to 0.8,
            id.toString() to 0.6,
            description to 0.5,
            code to 0.9
    )

    companion object {
        fun fromJson(json: Json) = Controller(
                json["id"]?.toString()?.toIntOrNull() ?: 0
        ).also { it.fromJson(json) }
    }
}