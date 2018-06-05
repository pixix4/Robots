package de.westermann.robots.server.connection

import de.westermann.robots.datamodel.IRobotClient
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

private const val seperator: String = "|"

fun List<String>.toByteArray(): ByteArray = joinToString(seperator).toByteArray(Charsets.UTF_8)
fun ByteArray.toStringList(): List<String> = toString(Charsets.UTF_8).split(seperator)

fun decodeMqtt(iClient: IRobotClient, message: List<String>) {
    when (message[0]) {
        IRobotClient::track.name -> {
            iClient.track(Track.parse(message[1]))
        }
        IRobotClient::speed.name -> {
            iClient.speed(message[1].toDouble())
        }
        IRobotClient::trim.name -> {
            iClient.trim(message[1].toDouble())
        }
        IRobotClient::pid.name -> {
            iClient.pid(message[1].toBoolean())
        }
        IRobotClient::resetMap.name -> {
            iClient.resetMap()
        }
        IRobotClient::setForegroundColor.name -> {
            iClient.setForegroundColor()
        }
        IRobotClient::setBackgroundColor.name -> {
            iClient.setBackgroundColor()
        }
        else -> {
            println("Unknown function ${message[0]}")
        }
    }
}

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
                    List::class -> value.split(";").map { Color.parse(it) }
                    Coordinate::class -> Coordinate.parse(value)
                    String::class -> value
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
                    else -> it.toString()
                }
            }
        }