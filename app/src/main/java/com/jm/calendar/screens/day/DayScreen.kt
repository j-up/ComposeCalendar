package com.jm.calendar.screens.day

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jm.calendar.R
import com.jm.calendar.component.CalendarItem
import com.jm.calendar.component.MonthBottomSheet
import com.jm.calendar.ext.formatToTwoDigits
import com.jm.calendar.ext.noRippleClickable
import com.jm.calendar.model.CalendarPresent
import com.jm.calendar.model.DayInfoPresent
import com.jm.calendar.util.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DayScreen(
    dayState: State<DayState>,
    onDetailClickListener: ((DayInfoPresent) -> Unit),
    onBackClickListener: (() -> Unit),
    bottomMonthClickListener: ((CalendarPresent) -> Unit),
    onInitListener: (() -> Unit),
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        when (val state = dayState.value) {
            is DayState.OnInit -> {
                var showBottomSheet by remember { mutableStateOf(false) }

                val defaultDate = state.dateMap.values.flatten().find { it.isSelected } ?: CalendarPresent.createTodayPresent()
                var selectedDate by remember { mutableStateOf(defaultDate) }
                val pagerState = rememberPagerState(initialPage = state.currentWeekInMonth) {
                    state.dateMap.size
                }

                LaunchedEffect(selectedDate) {
                    val page = DateUtils.getWeekOfMonth(
                        selectedDate.getLocalDate()
                    )

                    pagerState.scrollToPage(page)
                }

                TitleBar(
                    month = state.currentMonth,
                    onBackClickListener = {
                        onBackClickListener()
                    },
                    monthOnClick = {
                        showBottomSheet = !showBottomSheet
                    }
                )

                HorizontalCalendar(
                    modifier = Modifier
                        .wrapContentHeight()
                        .padding(horizontal = 20.dp),
                    pagerState = pagerState,
                    state = state,
                    onDayClickListener = { clickPresent ->
                        selectedDate = clickPresent
                    },
                    selectedDate = selectedDate
                )

                DaySchedule(
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 5.dp),
                    calendarPresent = selectedDate,
                    onDetailClickListener = onDetailClickListener
                )

                if (showBottomSheet) {
                    val scope = rememberCoroutineScope()
                    val sheetState = rememberModalBottomSheetState()

                    MonthBottomSheet(
                        todayClickListener = {
                            val todayPresent = CalendarPresent.createTodayPresent()

                            showBottomSheet = false

                            if(selectedDate == todayPresent) {
                                scope.launch {
                                    pagerState.scrollToPage(state.currentWeekInMonth)
                                }
                            } else {
                                if (selectedDate.month != todayPresent.month) {
                                    bottomMonthClickListener(todayPresent)
                                }

                                selectedDate = todayPresent
                            }
                        },
                        dayClickListener = {
                            showBottomSheet = false
                            selectedDate = it.copy(isSelected = true)

                            bottomMonthClickListener(it)
                        },
                        onDismissRequest = { showBottomSheet = false },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        sheetState = sheetState,
                        defaultDateMap = state.dateMap,
                        selectedDate = selectedDate
                    )
                }
            }

            DayState.OnClear -> {
                onInitListener()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCalendar(
    modifier: Modifier,
    state: DayState.OnInit,
    pagerState: PagerState,
    onDayClickListener: ((CalendarPresent) -> Unit),
    selectedDate: CalendarPresent,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { pageIndex ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .wrapContentHeight()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val dayList = state.dateMap.getOrDefault(pageIndex, emptyList())

            dayList.forEach { present ->
                CalendarItem(
                    present,
                    onDayClickListener,
                    selectedDate
                )
            }
        }
    }
}

@Composable
fun TitleBar(month: Int, onBackClickListener: (() -> Unit), monthOnClick: (() -> (Unit))) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        IconButton(
            onClick = {
                onBackClickListener()
            },
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .align(Alignment.Center)
                .noRippleClickable { monthOnClick() }
        ) {
            Text(
                text = "${month.formatToTwoDigits()}월",
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 8.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.arrow_down_gray),
                contentDescription = "Image",
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@Composable
fun DaySchedule(
    modifier: Modifier,
    calendarPresent: CalendarPresent,
    onDetailClickListener: ((DayInfoPresent) -> Unit),
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.verticalScroll(scrollState)
    ) {
        repeat(24) { time ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "${time.formatToTwoDigits()}:00",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .noRippleClickable {
                            onDetailClickListener(
                                DayInfoPresent(
                                    date = "${calendarPresent.month}월 ${calendarPresent.dayOfMonth}일 (${calendarPresent.dayOfWeek})",
                                    startTime = "${time.formatToTwoDigits()}:00",
                                    endTime = "${(time + 1).formatToTwoDigits()}:00"
                                )
                            )
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .border(BorderStroke(1.dp, Color.LightGray))
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(color = Color.LightGray)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }
}

