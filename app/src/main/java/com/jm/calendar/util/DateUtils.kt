package com.jm.calendar.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.WeekFields
import java.util.Locale

object DateUtils {
    fun getWeekCountOfMonth(year: Int, month: Int): Int {
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val lastDayOfMonth = firstDayOfMonth.plusMonths(1).minusDays(1)
        return lastDayOfMonth.get(WeekFields.of(Locale.KOREA).weekOfMonth())
    }

    fun getWeekOfMonth(date: LocalDate): Int {
        val currentMonth = date.monthValue
        val currentYear = date.year

        for (week in 0 until getWeekCountOfMonth(currentYear, currentMonth)) {
            if (isTodayInWeek(currentYear, currentMonth, week, date)) {
                return week
            }
        }

        return 0
    }

    private fun isTodayInWeek(year: Int, month: Int, week: Int, today: LocalDate): Boolean {
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value
        val startingWeek = if (firstDayOfWeek == DayOfWeek.SUNDAY.value) 0 else 1

        val weekStartDate = LocalDate.of(year, month, 1)
            .with(DayOfWeek.SUNDAY)
            .plusWeeks(week.toLong() - startingWeek)

        val weekEndDate = weekStartDate.plusDays(6)

        return today in weekStartDate..weekEndDate
    }
}