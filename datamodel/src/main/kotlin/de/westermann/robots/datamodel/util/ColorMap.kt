package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
data class ColorMap(
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