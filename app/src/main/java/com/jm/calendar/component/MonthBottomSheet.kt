package com.jm.calendar.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jm.calendar.ext.formatToTwoDigits
import com.jm.calendar.ext.noRippleClickable
import com.jm.calendar.model.CalendarPresent
import com.jm.calendar.provider.MonthDataProvider
import com.jm.calendar.theme.ResourceObject
import kotlinx.coroutines.launch

private val weekDayList = listOf("일", "월", "화", "수", "목", "금", "토")
private const val startPage = 100
private const val maxPage = 200

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MonthBottomSheet(
    todayClickListener: (() -> Unit),
    dayClickListener: ((CalendarPresent) -> Unit),
    onDismissRequest: (() -> Unit),
    modifier: Modifier,
    sheetState: SheetState,
    defaultDateMap: HashMap<Int, List<CalendarPresent>>,
    selectedDate: CalendarPresent,
) {
    val pagerState = rememberPagerState(initialPage = startPage) {
        maxPage
    }

    val present = defaultDateMap.values.flatten().find { it.isSelected } ?: CalendarPresent.createTodayPresent()

    val date = present.getLocalDate()

    var currentPage by remember { mutableIntStateOf(startPage) }
    var currentDate by remember { mutableStateOf(date) }

    // key: 202403 value
    //                  0 - 3(일) 4(월)
    //                  1 - 11(일) 12(월)
    val cachedDateMap: HashMap<String, HashMap<Int, List<CalendarPresent>>> by remember {
        mutableStateOf(HashMap<String, HashMap<Int, List<CalendarPresent>>>().apply {
            put("${date.year}${date.monthValue}", defaultDateMap)
        })
    }

    LaunchedEffect(pagerState.currentPage) {
        val addMonth = (pagerState.currentPage - currentPage).toLong()
        currentDate = currentDate.plusMonths(addMonth)
        currentPage = pagerState.currentPage

        if(cachedDateMap.getOrDefault("${currentDate.year}${currentDate.monthValue}", null) == null) {
            cachedDateMap["${currentDate.year}${currentDate.monthValue}"] = MonthDataProvider.createMonthData(currentDate)
        }
    }


    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState,
        modifier = modifier
    ) {
        HorizontalPager(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .animateContentSize(),
            state = pagerState
        ) {
            val bottomSheetScope = rememberCoroutineScope()

            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .wrapContentHeight()
                ) {
                    Text(
                        text = "${currentDate.year}년 ${currentDate.monthValue.formatToTwoDigits()}월",
                        fontSize = 20.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )

                    Box(modifier = Modifier
                        .wrapContentHeight()
                        .width(70.dp)
                        .height(30.dp)
                        .align(Alignment.TopEnd)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray,
                            shape = CircleShape
                        )
                        .background(
                            MaterialTheme.colors.background,
                            RoundedCornerShape(25.dp)
                        )
                        .noRippleClickable {
                            todayClickListener()
                            bottomSheetScope.launch {
                                sheetState.hide()
                            }
                        }) {
                        Text(text = "오늘로", modifier = Modifier.align(Alignment.Center))
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    modifier = Modifier.padding(bottom = 50.dp, start = 20.dp, end = 20.dp)
                ) {
                    WeekRow()

                    cachedDateMap["${currentDate.year}${currentDate.monthValue}"]?.values?.forEach { dayList ->
                        DayNumberRow(list = dayList, selectedDate = selectedDate) {
                            dayClickListener(it)

                            bottomSheetScope.launch {
                                sheetState.hide()
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DayNumberRow(
    list: List<CalendarPresent>,
    selectedDate: CalendarPresent,
    onClickListener: ((CalendarPresent) -> Unit),
) {
    val selectedColor = ResourceObject.LocalColors.current.selectedDay

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        list.forEach { present ->
            if (present.isCurrentMonth) {
                Text(
                    text = present.dayOfMonth,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(20.dp)
                        .noRippleClickable {
                            onClickListener(present)
                        }
                        .drawBehind {
                            drawCircle(
                                color = selectedColor,
                                radius = if (present == selectedDate) 40f else 0f
                            )
                        },
                    color = if (present == selectedDate) Color.White else Color.Black,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Box(modifier = Modifier.width(20.dp))
            }
        }
    }
}

@Composable
fun WeekRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        weekDayList.forEach {
            WeekText(it)
        }
    }
}

@Composable
fun WeekText(week: String) {
    Text(text = week, color = Color.Gray)
}
