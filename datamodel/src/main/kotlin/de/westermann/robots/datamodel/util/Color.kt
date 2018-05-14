package de.westermann.robots.datamodel.util

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

/**
 * @author lars
 */
data class Color(
        val red: Int = 0,
        val green: Int = 0,
        val blue: Int = 0,
        val alpha: Double = 1.0
) {

    private data class Hsl(val hue: Int, val saturation: Double, val lightness: Double) {

        fun lighten(percent: Double): Hsl = copy(lightness = max(lightness + percent, 1.0))
        fun darken(percent: Double): Hsl = copy(lightness = min(lightness - percent, 0.0))
        fun lightness(lightness: Double): Hsl = copy(lightness = lightness)

        fun toColor(): Color {
            val c = (1 - abs(2 * lightness - 1)) * saturation
            val x = c * (1 - abs((hue / 60.0) % 2 - 1))
            val m = lightness - c / 2.0

            val (r, g, b) = when (hue) {
                in 0..59 -> Triple(c, x, 0.0)
                in 60..119 -> Triple(x, c, 0.0)
                in 120..179 -> Triple(0.0, c, x)
                in 180..239 -> Triple(0.0, x, c)
                in 240..299 -> Triple(x, 0.0, c)
                in 300..359 -> Triple(c, 0.0, x)
                else -> Triple(0.0, 0.0, 0.0)
            }
            return Color(
                    round((r + m) * MAX_VALUE).toInt(),
                    round((g + m) * MAX_VALUE).toInt(),
                    round((b + m) * MAX_VALUE).toInt()
            )
        }
    }

    private fun toHsl(): Hsl {
        val r = red / MAX_VALUE.toDouble()
        val g = green / MAX_VALUE.toDouble()
        val b = blue / MAX_VALUE.toDouble()
        val cMax = max(r, max(g, b))
        val cMin = min(r, min(g, b))
        val delta = cMax - cMin
        val l = (cMax + cMin) / 2.0
        return Hsl(
                if (delta == 0.0) 0 else round(when (cMax) {
                    r -> 60 * (((g - b) / delta) % 6)
                    g -> 60 * (((b - r) / delta) + 2)
                    b -> 60 * (((r - g) / delta) + 4)
                    else -> 0.0
                }).toInt(),
                if (delta == 0.0) 0.0 else delta / (1 - abs(2 * l - 1)),
                l
        )
    }

    private val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F')
    private fun Int.toHex() = hexArray[ushr(4 * 1) and 0xF].toString() + hexArray[this and 0xF]

    val lumincane: Double = 0.2162 * red + 0.7152 * green + 0.0722 * blue

    fun lighten(percent: Double): Color =
            toHsl().lighten(percent).toColor()

    fun darken(percent: Double): Color =
            toHsl().darken(percent).toColor()

    fun lightness(lightness: Double): Color =
            toHsl().lightness(lightness).toColor()

    override fun toString(): String {
        return if (alpha >= 1) {
            "#${red.toHex()}${green.toHex()}${blue.toHex()}"
        } else "rgba($red, $green, $blue, $alpha)"
    }

    companion object {

        private const val MAX_VALUE = 255

        private const val FUNCTION_RGB = "rgb"
        private const val FUNCTION_RGBA = "rgba"
        private const val FUNCTION_LIGHTEN = "lighten"
        private const val FUNCTION_DARKEN = "darken"
        private const val ILLEGAL_PARAMETER_COUNT = "Illegal parameter count of method "

        private fun parseLighten(param: List<String>): Color = when {
            param.size == 2 -> parse(param[0]).lighten(param[1].toDouble())
            param.size == 1 -> parse(param[0])
            else -> throw IllegalArgumentException(ILLEGAL_PARAMETER_COUNT + FUNCTION_LIGHTEN)
        }

        private fun parseDarken(param: List<String>): Color = when {
            param.size == 2 -> parse(param[0]).darken(param[1].toDouble())
            param.size == 1 -> parse(param[0])
            else -> throw IllegalArgumentException(ILLEGAL_PARAMETER_COUNT + FUNCTION_DARKEN)
        }

        private fun parseRgb(param: List<String>): Color = when {
            param.size == 3 -> Color(param[0].toInt(), param[1].toInt(), param[2].toInt(), 1.0)
            param.size == 1 -> parse(param[0])
            else -> throw IllegalArgumentException(ILLEGAL_PARAMETER_COUNT + FUNCTION_RGB)
        }

        private fun parseRgba(param: List<String>): Color = when {
            param.size == 4 -> Color(param[0].toInt(), param[1].toInt(), param[2].toInt(), param[3].toDouble())
            param.size == 2 -> parse(param[0]).copy(alpha = param[1].toDouble())
            param.size == 1 -> parse(param[0])
            else -> throw IllegalArgumentException(ILLEGAL_PARAMETER_COUNT + FUNCTION_RGBA)
        }

        fun parse(color: String): Color {
            if (color.isEmpty()) {
                throw IllegalArgumentException("Color should not be empty!")
            }
            val trim = color.trim()

            return if (trim.startsWith("#")) {
                Color(
                        trim.substring(1, 3).toInt(16),
                        trim.substring(3, 5).toInt(16),
                        trim.substring(5, 7).toInt(16),
                        1.0
                )
            } else {
                val param = parseFunction(trim)
                val methodName = param[0]
                param.removeAt(0)

                when (methodName) {
                    FUNCTION_RGBA -> parseRgba(param)
                    FUNCTION_RGB -> parseRgb(param)
                    FUNCTION_LIGHTEN -> parseLighten(param)
                    FUNCTION_DARKEN -> parseDarken(param)
                    else -> throw IllegalArgumentException("Cannot parse color function '$methodName'")
                }
            }
        }

        /**
         * Parse string function to get function name and parameters
         *
         * @param function function to parse
         *
         * @return a list that contains the function name as first element and the parameters afterwards
         * @throws IllegalArgumentException if function cannot be parsed
         */
        private fun parseFunction(function: String): MutableList<String> {
            if (function.isEmpty()) {
                throw IllegalArgumentException("Color should not be empty!")
            }

            val list = mutableListOf<String>()

            var depth = 0
            var paramStart = 0
            function.forEachIndexed { i, c ->
                when (c) {
                    '(' -> {
                        if (list.isEmpty() && depth == 0) {
                            list.add(function.substring(0, i).trim { it <= ' ' })
                            paramStart = i + 1
                        }
                        depth++
                    }
                    ')' -> {
                        depth--
                        if (depth == 0) {
                            list.add(function.substring(paramStart, i).trim { it <= ' ' })
                            return list
                        } else if (depth < 0) {
                            throw IllegalArgumentException("Cannot parse color '$function'. Error at char $i")
                        }
                    }
                    ',' -> if (depth == 1) {
                        list.add(function.substring(paramStart, i).trim { it <= ' ' })
                        paramStart = i + 1
                    }
                    else -> {
                    }
                }
            }
            throw IllegalArgumentException("Cannot parse color '$function'. Error at char ${function.length}")
        }


        val TRANSPARENT = Color(0, 0, 0, 0.0)

        val WHITE = Color(MAX_VALUE, MAX_VALUE, MAX_VALUE)
        val BLACK = Color(0, 0, 0)

        val RED = Color.parse("#F44336")
        val PINK = Color.parse("#E91E63")
        val PURPLE = Color.parse("#9C27B0")
        val DEEP_PURPLE = Color.parse("#673AB7")
        val INDIGO = Color.parse("#3F51B5")
        val BLUE = Color.parse("#2196F3")
        val LIGHT_BLUE = Color.parse("#03A9F4")
        val CYAN = Color.parse("#00BCD4")
        val TEAL = Color.parse("#009688")
        val GREEN = Color.parse("#4CAF50")
        val LIGHT_GREEN = Color.parse("#8BC34A")
        val LIME = Color.parse("#CDDC39")
        val YELLOW = Color.parse("#FFEB3B")
        val AMBER = Color.parse("#FFC107")
        val ORANGE = Color.parse("#FF9800")
        val DEEP_ORANGE = Color.parse("#FF5722")
        val BROWN = Color.parse("#795548")
        val GREY = Color.parse("#9E9E9E")
        val BLUE_GREY = Color.parse("#607D8B")

    }
}
