package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
actual class Json(val obj: kotlin.js.Json) {
    actual operator fun get(propertyName: String): Any? = obj[propertyName]

    actual operator fun set(propertyName: String, value: Any?) {
        obj[propertyName] = value
    }

    actual fun value(propertyName: String, init: () -> Any?) = set(propertyName, init())

    actual fun json(propertyName: String): Json? = (get(propertyName) as? kotlin.js.Json)?.let { Json(it) }

    actual fun json(propertyName: String, init: Json.() -> Unit) = set(propertyName, Json.create(init))

    actual fun stringify(): String = JSON.stringify(obj)

    override fun toString(): String = stringify()

    actual companion object {
        actual fun create(init: Json.() -> Unit): Json = Json(kotlin.js.JSON.parse("{}")).also(init)

        actual fun fromString(data: String): Json = Json(kotlin.js.JSON.parse(data))
    }
}