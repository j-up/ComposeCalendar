package com.jm.calendar.screens.home

import com.jm.calendar.model.CalendarPresent

sealed class HomeState {
    data class OnInit(
        val dateMap: HashMap<Int, List<CalendarPresent>>,
        val currentWeekInMonth: Int,
        val currentMonth: Int,
    ) : HomeState()
    object OnClear: HomeState()
}