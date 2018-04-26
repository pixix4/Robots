package de.westermann.robots.server.utils

/**
 * @author lars
 */

fun String.toCamelCase(): String = split("[-_]".toRegex()).joinToString("") {
    it.toLowerCase().capitalize()
}

fun String.toDashCase(): String = replace("(.)([A-Z])".toRegex(), "$1-$2")
        .replace(" ", "-")
        .replace("-+".toRegex(), "-")
        .toLowerCase()

fun String.toUpperDashCase(): String = toDashCase().split("[-_]".toRegex()).joinToString("") {
    capitalize()
}