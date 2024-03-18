package com.jm.calendar.screens.day

import com.jm.calendar.model.CalendarPresent

sealed class DayState {
    data class OnInit(
        val dateMap: HashMap<Int, List<CalendarPresent>>,
        val currentWeekInMonth: Int,
        val currentMonth: Int,
    ) : DayState()
    object OnClear: DayState()
}