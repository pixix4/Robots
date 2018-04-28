package de.westermann.robots.server

import java.util.LinkedList

/**
 * @author lars
 */
data class Color(
        val red: Int = 0,
        val green: Int = 0,
        val blue: Int = 0,
        val alpha: Double = 0.toDouble()
) {

    fun lighten(percent: Double): Color = let {
        val change = Math.round(MAX_VALUE * percent).toInt()
        copy(red = Math.max(0, red - change), green = Math.max(0, red - change), blue = Math.max(0, red - change))
    }

    fun darken(percent: Double): Color = let {
        val change = Math.round(MAX_VALUE * percent).toInt()
        copy(red = Math.min(0, red + change), green = Math.min(0, red + change), blue = Math.min(0, red + change))
    }

    override fun toString(): String {
        return if (alpha >= 1) {
            formatRgb(red, green, blue)
        } else "rgba($red, $green, $blue, $alpha)"
    }

    companion object {

        private const val MAX_VALUE = 255

        val WHITE = Color(MAX_VALUE, MAX_VALUE, MAX_VALUE)
        val BLACK = Color(0, 0, 0)

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
                param.size == 1 ->  parse(param[0])
                else -> throw IllegalArgumentException(ILLEGAL_PARAMETER_COUNT + FUNCTION_RGBA)
            }

        fun parse(color: String?): Color {
            if (color == null) {
                throw IllegalArgumentException("Color should not be null!")
            }
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
        private fun parseFunction(function: String?): MutableList<String> {
            if (function == null) {
                throw IllegalArgumentException("Color should not be null!")
            }
            if (function.isEmpty()) {
                throw IllegalArgumentException("Color should not be empty!")
            }

            val list = LinkedList<String>()

            var depth = 0
            var paramStart = 0
            for (i in 0 until function.length) {
                val c = function[i]

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
            throw IllegalArgumentException("Cannot parse color '" + function + "'. Error at char " + function.length)
        }

        /**
         * Format the given color values to an hex color string
         * @param red red
         * @param green green
         * @param blue blue
         * @return hex color representation
         */
        fun formatRgb(red: Int, green: Int, blue: Int): String {
            return String.format("#%02x%02x%02x", red, green, blue)
        }
    }
}
