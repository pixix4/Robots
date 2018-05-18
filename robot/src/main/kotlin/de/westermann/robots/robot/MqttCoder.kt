package de.westermann.robots.robot

import de.westermann.robots.datamodel.util.*
import java.lang.reflect.Method
import java.nio.ByteBuffer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

/**
 * @author lars
 */

fun Int.toByteArray(): ByteArray =
        ByteBuffer.allocate(java.lang.Integer.BYTES)
                .putInt(this)
                .array()

fun ByteArray.toInt(): Int = ByteBuffer.wrap(this).int

private val seperator: String = "|"

fun List<String>.toByteArray(): ByteArray = joinToString(seperator).toByteArray(Charsets.UTF_8)
fun ByteArray.toStringList(): List<String> = toString(Charsets.UTF_8).split(seperator)

fun decodeMqtt(clazz: KClass<*>, message: List<String>): Pair<KFunction<*>, Array<Any?>>? {
    val func = clazz.functions.find {
        it.name == message[0]
    } ?: return null

    val p = func.parameters.drop(1)
            .zip(message.drop(1))
            .map { (param, value) ->
                @Suppress("IMPLICIT_CAST_TO_ANY")
                when (param.type.jvmErasure) {
                    Int::class -> value.toIntOrNull()
                    Double::class -> value.toDoubleOrNull()
                    Color::class -> Color.parse(value)
                    Track::class -> Track.parse(value)
                    Energy::class -> Energy.parse(value)
                    Version::class -> Version.parse(value)
                    Coordinates::class -> Coordinates.parse(value)
                    else -> null
                }
            }.toTypedArray()

    return func to p
}

fun encodeMqtt(method: Method, params: List<Any?>): List<String> = listOf(method.name) +
        params.map {
            if (it == null) {
                ""
            } else {
                when (it::class) {
                    Track::class -> (it as Track).toMqtt()
                    Energy::class -> (it as Energy).toMqtt()
                    Coordinates::class -> (it as Coordinates).toMqtt()
                    else -> it.toString()
                }
            }
        }