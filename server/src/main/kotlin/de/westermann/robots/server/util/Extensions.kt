package de.westermann.robots.server.util

/**
 * @author lars
 */

fun String.toPascalCase(): String = split("[-_]".toRegex()).joinToString("") {
    it.toLowerCase().capitalize()
}

fun String.toCamelCase(): String = toPascalCase().decapitalize()

fun String.toDashCase(): String = replace("(.)([A-Z])".toRegex(), "$1-$2")
        .replace(" ", "-")
        .replace("-+".toRegex(), "-")
        .toLowerCase()

fun String.toUpperDashCase(): String = toDashCase().split("[-_]".toRegex()).joinToString("") {
    capitalize()
}