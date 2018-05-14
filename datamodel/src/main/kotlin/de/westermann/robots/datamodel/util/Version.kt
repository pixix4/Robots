package de.westermann.robots.datamodel.util

/**
 * @author lars
 */

data class Version(
        val major: Int,
        val minor: Int,
        val patch: Int,
        val qualifier: Qualifier = Qualifier.NONE,
        val qualifierNumber: Int = 0
) : Comparable<Version> {

    val unknown: Boolean = this == UNKNOWN

    enum class Qualifier(val value: Int) {
        NONE(4),
        RC(3),
        BETA(2),
        ALPHA(1),
        SNAPSHOT(0);

        companion object {
            fun find(value: Int?): Qualifier =
                    Qualifier.values().find { it.value == value } ?: NONE

        }
    }

    private fun values() = listOf(major, minor, patch, qualifier.value, qualifierNumber)

    override fun compareTo(other: Version): Int = values().zip(other.values()).map {
        it.first.compareTo(it.second)
    }.find { it != 0 } ?: 0

    override fun toString(): String = if (unknown)
        "TRANSPARENT"
    else
        "$major.$minor.$patch" + if (qualifier != Qualifier.NONE) {
            qualifier.name.toLowerCase() + if (qualifierNumber != 0) qualifierNumber else ""
        } else ""

    fun toJson() = json {
        value("major") { major }
        value("minor") { minor }
        value("patch") { patch }
        value("qualifier") { qualifier.value }
        value("qualifierNumber") { qualifierNumber }
    }

    companion object {
        const val GROUP_MAJOR = 1
        const val GROUP_MINOR = 2
        const val GROUP_PATCH = 3
        const val GROUP_QUALIFIER = 4
        const val GROUP_QUALIFIER_NUMBER = 5

        val UNKNOWN = Version(0, 0, 0, Qualifier.NONE, 0)

        fun parse(value: String): Version =
                "([0-9]+).([0-9]+).([0-9]+)(?:-([A-Za-z]+)(?:.([0-9]+))?)?".toRegex()
                        .find(value)?.groups?.let { match ->
                    var v = Version(
                            match[GROUP_MAJOR]?.value?.toInt() ?: 0,
                            match[GROUP_MINOR]?.value?.toInt() ?: 0,
                            match[GROUP_PATCH]?.value?.toInt() ?: 0
                    )
                    match[GROUP_QUALIFIER]?.let { possibleQualifier ->
                        try {
                            Qualifier.valueOf(possibleQualifier.value.toUpperCase())
                        } catch (_: IllegalArgumentException) {
                            null
                        }?.let { qualifier ->
                            v = v.copy(qualifier = qualifier)
                            if (qualifier != Qualifier.NONE) {
                                match[GROUP_QUALIFIER_NUMBER]?.value?.toIntOrNull()?.let {
                                    v = v.copy(qualifierNumber = it)
                                }
                            }
                        }
                    }
                    v
                } ?: UNKNOWN

        fun fromJson(json: Json) = Version(
                json["major"]?.toString()?.toIntOrNull() ?: 0,
                json["minor"]?.toString()?.toIntOrNull() ?: 0,
                json["patch"]?.toString()?.toIntOrNull() ?: 0,
                Qualifier.find(json["qualifier"]?.toString()?.toIntOrNull()),
                json["qualifierNumber"]?.toString()?.toIntOrNull() ?: 0

        )
    }
}