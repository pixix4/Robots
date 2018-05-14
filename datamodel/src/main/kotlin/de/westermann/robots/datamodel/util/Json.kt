package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
expect class Json {

    operator fun get(propertyName: String): Any?

    operator fun set(propertyName: String, value: Any?)

    fun value(propertyName: String, init: () -> Any?)

    fun json(propertyName: String): Json?

    fun json(propertyName: String, init: Json.() -> Unit)

    fun stringify(): String

    companion object {
        fun create(init: Json.() -> Unit): Json
        fun fromString(data: String): Json
    }
}

fun json(init: Json.() -> Unit): Json = Json.create(init)