package com.jm.calendar.screens.dayinfo

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jm.calendar.model.DayInfoPresent

@Composable
fun DayInfoScreen(
    onBackClickListener: (() -> Unit),
    dayInfoPresent: DayInfoPresent,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        TitleBar(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(Color.White),
            onBackClickListener = onBackClickListener
        )

        Surface(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp)
                .align(Alignment.TopCenter)
                .wrapContentHeight()
                .padding(15.dp),
            border = BorderStroke(
                width = 1.dp,
                color = Color.LightGray
            ),
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                DayInfoRow("일자", dayInfoPresent.date)

                Divider(Color.LightGray)

                DayInfoRow("시작 시간", dayInfoPresent.startTime)

                Divider(Color.LightGray)

                DayInfoRow("종료 시간", dayInfoPresent.endTime)
            }
        }
    }
}

@Composable
fun TitleBar(modifier: Modifier, onBackClickListener: (() -> Unit)) {
    Box(
        modifier = modifier
    ) {

        IconButton(
            modifier = Modifier.align(Alignment.CenterStart),
            onClick = {
                onBackClickListener()
            }
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(20.dp)
            )
        }

        Text(
            text = "일정",
            fontSize = 15.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(end = 8.dp)
                .align(Alignment.Center)
        )
    }
}

@Composable
fun Divider(color: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(horizontal = 15.dp)
            .background(color)
    )
}

@Composable
fun DayInfoRow(title: String, content: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = title, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = content,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
        )
    }
}