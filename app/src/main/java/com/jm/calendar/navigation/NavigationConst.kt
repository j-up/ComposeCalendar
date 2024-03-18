package com.jm.calendar.navigation

sealed class NavigationConst(
    val route: String
) {
    object Home : NavigationConst(
        "Home"
    )

    object Day : NavigationConst(
        "Day"
    )

    object DayInfo : NavigationConst(
        "DayInfo"
    )
}