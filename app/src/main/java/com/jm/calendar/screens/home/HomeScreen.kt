package com.jm.calendar.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
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
import com.jm.calendar.util.DateUtils
import kotlinx.coroutines.launch

const val hoursCount = 24
const val weekCount = 7

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeState: State<HomeState>,
    onDetailClickListener: ((CalendarPresent) -> Unit),
    bottomMonthClickListener: ((CalendarPresent) -> Unit)
) {
    var isExtend by remember {
        mutableStateOf(false)
    }

    Surface(
        shape = RoundedCornerShape(
            bottomStart = 10.dp,
            bottomEnd = 10.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(bottom = 10.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color.LightGray
        ),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .animateContentSize()
                .padding(bottom = 30.dp)
        ) {
            when (val state = homeState.value) {
                is HomeState.OnInit -> {
                    var showBottomSheet by remember { mutableStateOf(false) }

                    val date = state.dateMap.values.flatten().find { it.isSelected } ?: CalendarPresent.createTodayPresent()
                    var selectedDate by remember { mutableStateOf(date) }

                    val pagerState = rememberPagerState {
                        state.dateMap.size
                    }

                    LaunchedEffect(selectedDate) {
                        val page = DateUtils.getWeekOfMonth(
                            selectedDate.getLocalDate()
                        )

                        pagerState.scrollToPage(page)
                    }

                    TitleBar(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp)
                            .wrapContentHeight(),
                        isExtend = isExtend,
                        extendOnClick = {
                            isExtend = !isExtend
                        },
                        month = state.currentMonth,
                        monthOnClick = {
                            showBottomSheet = !showBottomSheet
                        }
                    )

                    val pagerScope = rememberCoroutineScope()

                    HorizontalCalendar(
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(horizontal = 20.dp),
                        state = state,
                        pagerState = pagerState,
                        isExtend = isExtend,
                        selectedDate = selectedDate,
                        onDayClickListener = { clickPresent ->
                            selectedDate = clickPresent
                        },
                        todayClickListener = {
                            val todayPresent = CalendarPresent.createTodayPresent()

                            if (selectedDate == todayPresent) {
                                pagerScope.launch {
                                    pagerState.scrollToPage(state.currentWeekInMonth)
                                }
                            } else {
                                if (selectedDate.month != todayPresent.month) {
                                    bottomMonthClickListener(todayPresent)
                                }

                                selectedDate = todayPresent
                            }
                        }
                    )

                    if (isExtend) {
                        DaySchedule(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(350.dp)
                                .padding(horizontal = 20.dp),
                            onDetailClickListener = onDetailClickListener,
                            list = state.dateMap[pagerState.currentPage]
                        )
                    }


                    if (showBottomSheet) {
                        val sheetState = rememberModalBottomSheetState()
                        val sheetScope = rememberCoroutineScope()

                        MonthBottomSheet(
                            todayClickListener = {
                                val todayPresent = CalendarPresent.createTodayPresent()

                                showBottomSheet = false

                                if(selectedDate == todayPresent) {
                                    sheetScope.launch {
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

                HomeState.OnClear -> {

                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCalendar(
    modifier: Modifier,
    state: HomeState.OnInit,
    pagerState: PagerState,
    isExtend: Boolean,
    selectedDate: CalendarPresent,
    onDayClickListener: ((CalendarPresent) -> Unit),
    todayClickListener: (() -> Unit)
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState
    ) { pageIndex ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isExtend) {
                TodayRefreshButton(
                    Modifier
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(5.dp)
                        .wrapContentHeight()
                        .noRippleClickable {
                            todayClickListener()
                        }
                )
            }

            val dayList = state.dateMap.getOrDefault(pageIndex, emptyList())

            dayList.forEach { present ->
                CalendarItem(
                    model = present,
                    onDayClickListener = { clickPresent ->
                        onDayClickListener(clickPresent)
                    },
                    selectedPresent = selectedDate
                )
            }
        }
    }
}

@Composable
fun TitleBar(
    modifier: Modifier,
    isExtend: Boolean,
    extendOnClick: (() -> (Unit)),
    month: Int,
    monthOnClick: (() -> (Unit)),
) {
    Box(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .noRippleClickable {
                    monthOnClick()
                },
            verticalAlignment = Alignment.CenterVertically,
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

        Text(
            text = if (isExtend) "주간 일정 접기" else "주간 일정 편치기",
            color = Color.Gray,
            modifier = Modifier
                .noRippleClickable {
                    extendOnClick()
                }
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun TodayRefreshButton(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "오늘")
        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
    }
}

@Composable
fun DaySchedule(
    modifier: Modifier,
    onDetailClickListener: ((CalendarPresent) -> Unit),
    list: List<CalendarPresent>?,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(modifier = Modifier.padding(start = 20.dp, end = 25.dp)) {
            items(hoursCount) {
                Text(
                    text = it.formatToTwoDigits(),
                    fontSize = 15.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 15.dp)
                )
            }
        }

        repeat(weekCount) { weekIndex ->
            LazyColumn {
                items(1) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(350.dp)
                            .drawBehind {
                                val strokeWidth = 2f
                                val y = size.height - strokeWidth
                                drawLine(
                                    color = Color.LightGray,
                                    start = Offset(0f, 0f),
                                    end = Offset(0f, y),
                                    strokeWidth = strokeWidth
                                )
                            }
                            .noRippleClickable {
                                list
                                    ?.get(weekIndex)
                                    ?.let { present ->
                                        onDetailClickListener(present)
                                    }
                            }
                    )
                }
            }
        }
    }
}

