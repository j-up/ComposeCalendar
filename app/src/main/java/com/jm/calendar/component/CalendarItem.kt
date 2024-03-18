package com.jm.calendar.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jm.calendar.ext.conditional
import com.jm.calendar.model.CalendarPresent
import com.jm.calendar.theme.ResourceObject

@Composable
fun CalendarItem(model: CalendarPresent, onDayClickListener: ((CalendarPresent) -> Unit), selectedPresent: CalendarPresent?) {
    val selectedColor = ResourceObject.LocalColors.current.selectedDay
    val unselectedColor = ResourceObject.LocalColors.current.unselectedDay
    val isSelected = selectedPresent == model
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = Modifier.clickable(
            enabled = model.isCurrentMonth,
            interactionSource = interactionSource,
            indication = null
        ) {
            if (model.isCurrentMonth) {
                onDayClickListener(model)
            }
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = model.dayOfWeek,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) selectedColor else unselectedColor
        )
        Text(
            text = model.dayOfMonth,
            modifier = Modifier.conditional(isSelected) {
                drawBehind {
                    drawCircle(
                        color = selectedColor,
                        radius = 40f
                    )
                }
            },
            color = when {
                !model.isCurrentMonth -> Color.LightGray
                isSelected -> Color.White
                else -> Color.Gray
            }
        )
    }
}