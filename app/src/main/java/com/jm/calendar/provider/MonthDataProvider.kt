package com.jm.calendar.provider

import com.jm.calendar.model.CalendarPresent
import com.jm.calendar.util.DateUtils
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class MonthDataProvider {
    companion object {
        // key - 0 (번쨰주) 3(일) 4(월)
        // key - 0 (번쨰주) 3(일) 4(월)
        // key - 0 (번쨰주) 3(일) 4(월)
        // key - 0 (번쨰주) 3(일) 4(월)
        // key - 0 (번쨰주) 3(일) 4(월)

        fun createMonthData(date: LocalDate): HashMap<Int, List<CalendarPresent>> {
            val dateMap = HashMap<Int, List<CalendarPresent>>()

            val weekCount = DateUtils.getWeekCountOfMonth(date.year, date.monthValue)

            val startingWeek = run {
                val firstDayOfMonth = LocalDate.of(date.year, date.monthValue, 1)
                val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

                if (firstDayOfWeek == DayOfWeek.SUNDAY.value) 0 else 1
            }

            for (week in 0 until weekCount) {
                val weekDates = generateWeekDates(date.year, date.monthValue, startingWeek, week, date)
                dateMap[week] = weekDates
            }

            return dateMap
        }

        private fun generateWeekDates(year: Int, month: Int, startingWeek: Int, week: Int, selectedDate: LocalDate): List<CalendarPresent> {
            val weekDates = mutableListOf<CalendarPresent>()
            for (dayOfWeek in DayOfWeek.MONDAY.value..DayOfWeek.SUNDAY.value) {
                val formattedDate = LocalDate.of(year, month, 1)
                    .with(DayOfWeek.SUNDAY)
                    .plusWeeks(week.toLong() - startingWeek)
                    .plusDays(dayOfWeek.toLong() - DayOfWeek.MONDAY.value.toLong())

                weekDates.add(
                    CalendarPresent(
                        year = formattedDate.year.toString(),
                        dayOfWeek = formattedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.KOREA),
                        dayOfMonth = formattedDate.dayOfMonth.toString(),
                        month = formattedDate.monthValue.toString(),
                        isSelected = formattedDate == selectedDate,
                        isCurrentMonth = month == formattedDate.monthValue
                    )
                )
            }
            return weekDates
        }
    }
}