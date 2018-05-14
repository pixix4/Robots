package de.westermann.robots.datamodel.util

/**
 * @author lars
 */
enum class ColorDefaults {
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
    DEFAULT;

    companion object {

        private fun load(
                primaryColor: String,
                primaryColorText: Brightness,
                primaryColorDark: String,
                primaryColorDarkText: Brightness,
                primaryColorLight: String,
                primaryColorLightText: Brightness
        ): ColorMap = ColorMap(
                Color.parse(primaryColor),
                primaryColorText,
                Color.parse(primaryColorDark),
                primaryColorDarkText,
                Color.parse(primaryColorLight),
                primaryColorLightText,
                Color.WHITE,
                Color.parse("#F4F4F4"),
                Brightness.DARK
        )

        operator fun get(index: ColorDefaults): ColorMap = when (index) {
            RED -> load("#F44336", Brightness.LIGHT, "#D32F2F", Brightness.LIGHT, "#FFCDD2", Brightness.DARK)
            PINK -> load("#E91E63", Brightness.LIGHT, "#C2185B", Brightness.LIGHT, "#F8BBD0", Brightness.DARK)
            PURPLE -> load("#9C27B0", Brightness.LIGHT, "#7B1FA2", Brightness.LIGHT, "#E1BEE7", Brightness.DARK)
            DEEP_PURPLE -> load("#673AB7", Brightness.LIGHT, "#512DA8", Brightness.LIGHT, "#D1C4E9", Brightness.DARK)
            INDIGO -> load("#3F51B5", Brightness.LIGHT, "#303F9F", Brightness.LIGHT, "#C5CAE9", Brightness.DARK)
            BLUE -> load("#2196F3", Brightness.DARK, "#1976D2", Brightness.LIGHT, "#BBDEFB", Brightness.DARK)
            LIGHT_BLUE -> load("#03A9F4", Brightness.DARK, "#0288D1", Brightness.LIGHT, "#B3E5FC", Brightness.DARK)
            CYAN -> load("#00BCD4", Brightness.DARK, "#0097A7", Brightness.LIGHT, "#B2EBF2", Brightness.DARK)
            TEAL -> load("#009688", Brightness.LIGHT, "#00796B", Brightness.LIGHT, "#B2DFDB", Brightness.DARK)
            GREEN -> load("#4CAF50", Brightness.DARK, "#388E3C", Brightness.LIGHT, "#C8E6C9", Brightness.DARK)
            LIGHT_GREEN -> load("#8BC34A", Brightness.DARK, "#689F38", Brightness.DARK, "#DCEDC8", Brightness.DARK)
            LIME -> load("#CDDC39", Brightness.DARK, "#AFB42B", Brightness.DARK, "#F0F4C3", Brightness.DARK)
            YELLOW -> load("#FFEB3B", Brightness.DARK, "#FBC02D", Brightness.DARK, "#FFF9C4", Brightness.DARK)
            AMBER -> load("#FFC107", Brightness.DARK, "#FFA000", Brightness.DARK, "#FFECB3", Brightness.DARK)
            ORANGE -> load("#FF9800", Brightness.DARK, "#F57C00", Brightness.DARK, "#FFE0B2", Brightness.DARK)
            DEEP_ORANGE -> load("#FF5722", Brightness.DARK, "#E64A19", Brightness.LIGHT, "#FFCCBC", Brightness.DARK)
            BROWN -> load("#795548", Brightness.LIGHT, "#5D4037", Brightness.LIGHT, "#D7CCC8", Brightness.DARK)
            GREY -> load("#9E9E9E", Brightness.DARK, "#616161", Brightness.LIGHT, "#F5F5F5", Brightness.DARK)
            BLUE_GREY -> load("#607D8B", Brightness.LIGHT, "#455A64", Brightness.LIGHT, "#CFD8DC", Brightness.DARK)
            DEFAULT -> load("#274275", Brightness.LIGHT, "#002557", Brightness.LIGHT, "#CFD8DC", Brightness.DARK)
        }
    }
}