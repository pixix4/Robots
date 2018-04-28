package de.westermann.robots.server

import de.westermann.robots.server.utils.Configuration
import de.westermann.robots.server.utils.Printer
import mu.KotlinLogging


/**
 * @author lars
 */
object ColorScheme {
    private val logger = KotlinLogging.logger {}

    val primaryColor: Color
    val primaryColorText: Brightness

    val primaryColorDark: Color
    val primaryColorDarkText: Brightness

    val primaryColorLight: Color
    val primaryColorLightText: Brightness

    val backgroundColorPrimary: Color
    val backgroundColorSecondary: Color
    val textColor: Brightness

    private fun load(
            primaryColor: String,
            primaryColorText: Brightness,
            primaryColorDark: String,
            primaryColorDarkText: Brightness,
            primaryColorLight: String,
            primaryColorLightText: Brightness
    ): ColorMap = load(
            Color.parse(primaryColor),
            primaryColorText,
            Color.parse(primaryColorDark),
            primaryColorDarkText,
            Color.parse(primaryColorLight),
            primaryColorLightText
    )

    private fun load(
            primaryColor: Color,
            primaryColorText: Brightness,
            primaryColorDark: Color,
            primaryColorDarkText: Brightness,
            primaryColorLight: Color,
            primaryColorLightText: Brightness
    ): ColorMap = ColorMap(
            primaryColor,
            primaryColorText,
            primaryColorDark,
            primaryColorDarkText,
            primaryColorLight,
            primaryColorLightText,
            Color.WHITE,
            Color.parse("#F4F4F4"),
            Brightness.DARK
    )


    init {
        var map = when (Configuration.properties.colorScheme) {
            ColorScheme.Defaults.RED -> load("#F44336", Brightness.LIGHT, "#D32F2F", Brightness.LIGHT, "#FFCDD2", Brightness.DARK)
            ColorScheme.Defaults.PINK -> load("#E91E63", Brightness.LIGHT, "#C2185B", Brightness.LIGHT, "#F8BBD0", Brightness.DARK)
            ColorScheme.Defaults.PURPLE -> load("#9C27B0", Brightness.LIGHT, "#7B1FA2", Brightness.LIGHT, "#E1BEE7", Brightness.DARK)
            ColorScheme.Defaults.DEEP_PURPLE -> load("#673AB7", Brightness.LIGHT, "#512DA8", Brightness.LIGHT, "#D1C4E9", Brightness.DARK)
            ColorScheme.Defaults.INDIGO -> load("#3F51B5", Brightness.LIGHT, "#303F9F", Brightness.LIGHT, "#C5CAE9", Brightness.DARK)
            ColorScheme.Defaults.BLUE -> load("#2196F3", Brightness.DARK, "#1976D2", Brightness.LIGHT, "#BBDEFB", Brightness.DARK)
            ColorScheme.Defaults.LIGHT_BLUE -> load("#03A9F4", Brightness.DARK, "#0288D1", Brightness.LIGHT, "#B3E5FC", Brightness.DARK)
            ColorScheme.Defaults.CYAN -> load("#00BCD4", Brightness.DARK, "#0097A7", Brightness.LIGHT, "#B2EBF2", Brightness.DARK)
            ColorScheme.Defaults.TEAL -> load("#009688", Brightness.LIGHT, "#00796B", Brightness.LIGHT, "#B2DFDB", Brightness.DARK)
            ColorScheme.Defaults.GREEN -> load("#4CAF50", Brightness.DARK, "#388E3C", Brightness.LIGHT, "#C8E6C9", Brightness.DARK)
            ColorScheme.Defaults.LIGHT_GREEN -> load("#8BC34A", Brightness.DARK, "#689F38", Brightness.DARK, "#DCEDC8", Brightness.DARK)
            ColorScheme.Defaults.LIME -> load("#CDDC39", Brightness.DARK, "#AFB42B", Brightness.DARK, "#F0F4C3", Brightness.DARK)
            ColorScheme.Defaults.YELLOW -> load("#FFEB3B", Brightness.DARK, "#FBC02D", Brightness.DARK, "#FFF9C4", Brightness.DARK)
            ColorScheme.Defaults.AMBER -> load("#FFC107", Brightness.DARK, "#FFA000", Brightness.DARK, "#FFECB3", Brightness.DARK)
            ColorScheme.Defaults.ORANGE -> load("#FF9800", Brightness.DARK, "#F57C00", Brightness.DARK, "#FFE0B2", Brightness.DARK)
            ColorScheme.Defaults.DEEP_ORANGE -> load("#FF5722", Brightness.DARK, "#E64A19", Brightness.LIGHT, "#FFCCBC", Brightness.DARK)
            ColorScheme.Defaults.BROWN -> load("#795548", Brightness.LIGHT, "#5D4037", Brightness.LIGHT, "#D7CCC8", Brightness.DARK)
            ColorScheme.Defaults.GREY -> load("#9E9E9E", Brightness.DARK, "#616161", Brightness.LIGHT, "#F5F5F5", Brightness.DARK)
            ColorScheme.Defaults.BLUE_GREY -> load("#607D8B", Brightness.LIGHT, "#455A64", Brightness.LIGHT, "#CFD8DC", Brightness.DARK)
            ColorScheme.Defaults.CUSTOM -> load(Color.WHITE, Brightness.DARK, Color.WHITE, Brightness.DARK, Color.WHITE, Brightness.DARK)
        }

        Configuration.properties.primaryColor?.let {
            map = map.copy(primaryColor = it)
        }
        Configuration.properties.primaryColorText?.let {
            map = map.copy(primaryColorText = it)
        }

        Configuration.properties.primaryColorDark?.let {
            map = map.copy(primaryColorDark = it)
        }
        Configuration.properties.primaryColorDarkText?.let {
            map = map.copy(primaryColorDarkText = it)
        }

        Configuration.properties.primaryColorLight?.let {
            map = map.copy(primaryColorLight = it)
        }
        Configuration.properties.primaryColorLightText?.let {
            map = map.copy(primaryColorLightText = it)
        }

        Configuration.properties.backgroundColorPrimary?.let {
            map = map.copy(backgroundColorPrimary = it)
        }
        Configuration.properties.backgroundColorSecondary?.let {
            map = map.copy(backgroundColorSecondary = it)
        }
        Configuration.properties.textColor?.let {
            map = map.copy(textColor = it)
        }

        primaryColor = map.primaryColor
        primaryColorText = map.primaryColorText

        primaryColorDark = map.primaryColorDark
        primaryColorDarkText = map.primaryColorDarkText

        primaryColorLight = map.primaryColorLight
        primaryColorLightText = map.primaryColorLightText

        backgroundColorPrimary = map.backgroundColorPrimary
        backgroundColorSecondary = map.backgroundColorSecondary
        textColor = map.textColor
    }

    private enum class ColorOpacityLevel {
        PRIMARY, SECONDARY, DISABLED, DIVIDER, ICON, ICON_DISABLED
    }

    enum class Brightness {
        LIGHT, DARK
    }

    enum class Defaults {
        RED,
        PINK,
        PURPLE,
        DEEP_PURPLE,
        INDIGO,
        BLUE,
        LIGHT_BLUE,
        CYAN,
        TEAL,
        GREEN,
        LIGHT_GREEN,
        LIME,
        YELLOW,
        AMBER,
        ORANGE,
        DEEP_ORANGE,
        BROWN,
        GREY,
        BLUE_GREY,
        CUSTOM
    }

    private data class ColorMap(
            val primaryColor: Color,
            val primaryColorText: Brightness,

            val primaryColorDark: Color,
            val primaryColorDarkText: Brightness,

            val primaryColorLight: Color,
            val primaryColorLightText: Brightness,

            val backgroundColorPrimary: Color,
            val backgroundColorSecondary: Color,
            val textColor: Brightness
    )

    private fun getTextColor(brightness: Brightness, level: ColorOpacityLevel): Color = when (brightness) {
        Brightness.DARK -> {
            Color.BLACK.let {
                when (level) {
                    ColorScheme.ColorOpacityLevel.PRIMARY -> it.copy(alpha = 0.87)
                    ColorScheme.ColorOpacityLevel.SECONDARY -> it.copy(alpha = 0.54)
                    ColorScheme.ColorOpacityLevel.DISABLED -> it.copy(alpha = 0.38)
                    ColorScheme.ColorOpacityLevel.DIVIDER -> it.copy(alpha = 0.12)
                    ColorScheme.ColorOpacityLevel.ICON -> it.copy(alpha = 0.54)
                    ColorScheme.ColorOpacityLevel.ICON_DISABLED -> it.copy(alpha = 0.38)
                }
            }

        }
        Brightness.LIGHT -> {
            Color.WHITE.let {
                when (level) {
                    ColorScheme.ColorOpacityLevel.PRIMARY -> it.copy(alpha = 1.0)
                    ColorScheme.ColorOpacityLevel.SECONDARY -> it.copy(alpha = 0.7)
                    ColorScheme.ColorOpacityLevel.DISABLED -> it.copy(alpha = 0.5)
                    ColorScheme.ColorOpacityLevel.DIVIDER -> it.copy(alpha = 0.12)
                    ColorScheme.ColorOpacityLevel.ICON -> it.copy(alpha = 1.0)
                    ColorScheme.ColorOpacityLevel.ICON_DISABLED -> it.copy(alpha = 0.5)
                }
            }
        }
    }

    private fun getTextColorSet(brightness: Brightness, prefix: String): Map<String, String> = mapOf(
            "$prefix-text-primary" to getTextColor(brightness, ColorOpacityLevel.PRIMARY).toString(),
            "$prefix-text-secondary" to getTextColor(brightness, ColorOpacityLevel.SECONDARY).toString(),
            "$prefix-text-disabled" to getTextColor(brightness, ColorOpacityLevel.DISABLED).toString(),
            "$prefix-divider" to getTextColor(brightness, ColorOpacityLevel.DIVIDER).toString(),
            "$prefix-icon" to getTextColor(brightness, ColorOpacityLevel.ICON).toString(),
            "$prefix-icon-disabled" to getTextColor(brightness, ColorOpacityLevel.ICON_DISABLED).toString()
    )

    private fun getPrimaryColorSet(color: Color, brightness: Brightness, prefix: String): Map<String, String> =
            mapOf(prefix to color.toString()) + getTextColorSet(brightness, prefix)

    val colorMap: Map<String, String>
        get() = getPrimaryColorSet(primaryColor, primaryColorText, "\$primary-color") +
                getPrimaryColorSet(primaryColorDark, primaryColorDarkText, "\$primary-dark-color") +
                getPrimaryColorSet(primaryColorLight, primaryColorLightText, "\$primary-light-color") +
                getTextColorSet(textColor, "\$main") +
                mapOf(
                        "\$background-primary" to backgroundColorPrimary.toString(),
                        "\$background-secondary" to backgroundColorSecondary.toString()
                )


    fun log(l: (String) -> Unit = {
        logger.info { it }
    }) = Printer.Table(
            "Color Scheme '${Configuration.properties.colorScheme}'",
            colorMap.map {
                Printer.Line("${it.key}:", it.value)
            }
    ).log(l)
}
