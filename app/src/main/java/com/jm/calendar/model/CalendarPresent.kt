package com.jm.calendar.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Parcelize
data class CalendarPresent(
    val year: String,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val month: String,
    val isSelected: Boolean,
    val isCurrentMonth: Boolean
) : Parcelable {

    fun getLocalDate(): LocalDate = LocalDate.of(year.toInt(), month.toInt(), dayOfMonth.toInt())

    companion object {
        fun createTodayPresent(): CalendarPresent {
            val today = LocalDate.now()
            return CalendarPresent(
                year = today.year.toString(),
                dayOfWeek = today.dayOfWeek.getDisplayName(
                    TextStyle.SHORT,
                    Locale.KOREA
                ),
                dayOfMonth = today.dayOfMonth.toString(),
                month = today.monthValue.toString(),
                isSelected = true,
                isCurrentMonth = true
            )
        }
    }
}

