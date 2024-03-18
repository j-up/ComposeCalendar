package com.jm.calendar.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jm.calendar.provider.MonthDataProvider
import com.jm.calendar.model.CalendarPresent
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
class HomeViewModel @Inject constructor() : ViewModel() {
    private val todayDate = LocalDate.now()

    private val _homeStateFlow = MutableStateFlow<HomeState>(
        HomeState.OnInit(
            dateMap = MonthDataProvider.createMonthData(todayDate),
            currentWeekInMonth = DateUtils.getWeekOfMonth(todayDate),
            currentMonth = todayDate.monthValue
        )
    )

    val homeStateFlow: StateFlow<HomeState> = _homeStateFlow.asStateFlow()


    fun monthUpdate(present: CalendarPresent) = viewModelScope.launch(Dispatchers.IO) {
        val localDate = present.getLocalDate()

        _homeStateFlow.emit(
            HomeState.OnInit(
                dateMap = MonthDataProvider.createMonthData(localDate),
                currentWeekInMonth = DateUtils.getWeekOfMonth(localDate),
                currentMonth = localDate.monthValue
            )
        )
    }
}