package com.jm.calendar.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jm.calendar.R

@Immutable
data class CalendarTypography(
    private val colors: CalendarColors,
    val baseTextStyle: TextStyle = TextStyle(
        color = colors.primary,
        fontFamily = CalendarGrotesk
    ),
    val h1: TextStyle = baseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 30.sp,
        letterSpacing = 0.sp
    ),
    val h2: TextStyle = baseTextStyle.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        letterSpacing = 0.5.sp
    )
)

private val CalendarGrotesk = FontFamily(
    Font(R.font.nanum_gothic_extra_bold, FontWeight.W700),
    Font(R.font.nanum_gothic_bold, FontWeight.W600),
    Font(R.font.nanum_gothic_light, FontWeight.W200),
    Font(R.font.nanum_gothic, FontWeight.W500),
)
