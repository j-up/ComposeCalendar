package com.jm.calendar.screens.day

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jm.calendar.model.CalendarPresent
import com.jm.calendar.provider.MonthDataProvider
import com.jm.calendar.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DayViewModel @Inject constructor() : ViewModel() {

    private val _dayStateFlow = MutableStateFlow<DayState>(DayState.OnClear)

    val dayStateFlow: StateFlow<DayState> = _dayStateFlow.asStateFlow()

    fun initDate(date: LocalDate) = viewModelScope.launch(Dispatchers.IO) {
        _dayStateFlow.emit(
            DayState.OnInit(
                dateMap = MonthDataProvider.createMonthData(date),
                currentWeekInMonth = DateUtils.getWeekOfMonth(date),
                currentMonth = date.monthValue
            )
        )
    }

    fun monthUpdate(present: CalendarPresent) = viewModelScope.launch(Dispatchers.IO) {
        val localDate = present.getLocalDate()

        _dayStateFlow.emit(
            DayState.OnInit(
                dateMap = MonthDataProvider.createMonthData(localDate),
                currentWeekInMonth = DateUtils.getWeekOfMonth(localDate),
                currentMonth = localDate.monthValue
            )
        )
    }
}