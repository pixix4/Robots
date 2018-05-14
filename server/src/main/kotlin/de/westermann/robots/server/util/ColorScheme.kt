package de.westermann.robots.server.util

import de.westermann.robots.datamodel.util.Brightness
import de.westermann.robots.datamodel.util.Color
import de.westermann.robots.datamodel.util.ColorDefaults
import de.westermann.robots.datamodel.util.ColorMap
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

    init {
        var map = ColorDefaults[Configuration.properties.colorScheme]

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

    private fun getTextColor(brightness: Brightness, level: ColorOpacityLevel): Color = when (brightness) {
        Brightness.DARK -> {
            Color.BLACK.let {
                when (level) {
                    ColorOpacityLevel.PRIMARY -> it.copy(alpha = 0.87)
                    ColorOpacityLevel.SECONDARY -> it.copy(alpha = 0.54)
                    ColorOpacityLevel.DISABLED -> it.copy(alpha = 0.38)
                    ColorOpacityLevel.DIVIDER -> it.copy(alpha = 0.12)
                    ColorOpacityLevel.ICON -> it.copy(alpha = 0.54)
                    ColorOpacityLevel.ICON_DISABLED -> it.copy(alpha = 0.38)
                }
            }

        }
        Brightness.LIGHT -> {
            Color.WHITE.let {
                when (level) {
                    ColorOpacityLevel.PRIMARY -> it.copy(alpha = 1.0)
                    ColorOpacityLevel.SECONDARY -> it.copy(alpha = 0.7)
                    ColorOpacityLevel.DISABLED -> it.copy(alpha = 0.5)
                    ColorOpacityLevel.DIVIDER -> it.copy(alpha = 0.12)
                    ColorOpacityLevel.ICON -> it.copy(alpha = 1.0)
                    ColorOpacityLevel.ICON_DISABLED -> it.copy(alpha = 0.5)
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
