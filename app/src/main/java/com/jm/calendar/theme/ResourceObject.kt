package com.jm.calendar.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

object ResourceObject {
    val LocalColors = staticCompositionLocalOf { lightCalendarColors }
    val LocalTypography = staticCompositionLocalOf { CalendarTypography(lightCalendarColors) }

    @Composable
    fun CalendarTheme(
        isDarkMode: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val colors = if (isDarkMode) darkCalendarColors else lightCalendarColors
        val typography = CalendarTypography(colors)

        CompositionLocalProvider(
            LocalColors provides colors,
            LocalTypography provides typography,
            content = content
        )
    }
}
