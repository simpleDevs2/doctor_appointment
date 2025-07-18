package com.example.doctorappoint.ui.service

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.example.doctorappoint.ui.theme.SecondaryColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale


@Composable
fun BookingDateScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val currentSystemMonth = YearMonth.from(today)

    var displayedMonth by remember { mutableStateOf(currentSystemMonth) }
    var selectedDates by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }


    val isPrevMonthButtonEnabled = displayedMonth.isAfter(currentSystemMonth)
    var title = "Chọn ngày khám"
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            modifier,
            title,
            onBackClick = {
                 navController.popBackStack()
            }
        )
        SpacerHeight(18.dp)
        CalendarHeader(
            yearMonth = displayedMonth,
            isPrevEnabled = isPrevMonthButtonEnabled,
            onPreviousMonth = { displayedMonth = displayedMonth.minusMonths(1) },
            onNextMonth = { displayedMonth = displayedMonth.plusMonths(1) }
        )
        Spacer(modifier = Modifier.height(16.dp))


        CalendarGrid(
            yearMonth = displayedMonth,
            today = today,
            selectedDates = selectedDates,
            onDateSelected = { date ->
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("selected_date", date.toString())
                navController.popBackStack()
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        CalendarLegend()
    }
}

@Composable
fun CalendarHeader(
    yearMonth: YearMonth,
    isPrevEnabled: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val vietnameseLocale = Locale("vi", "VN")
    val monthName = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, vietnameseLocale)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onPreviousMonth, enabled = isPrevEnabled) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Tháng trước")
        }
        Text(
            text = "Tháng ${monthName.replaceFirstChar { it.titlecase(vietnameseLocale) }} - ${yearMonth.year}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = onNextMonth,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = Color(0xFF007BFF),
                contentColor = Color.White
            )
        ) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Tháng sau")
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    today: LocalDate,
    selectedDates: Set<LocalDate>,
    onDateSelected: (LocalDate) -> Unit
) {
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = yearMonth.atDay(1)
    val firstDayOfWeekValue = firstDayOfMonth.dayOfWeek.value % 7
    val daysOfWeek = listOf("CN", "T2", "T3", "T4", "T5", "T6", "T7")

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        val totalCells = (firstDayOfWeekValue + daysInMonth + 6) / 7 * 7
        for (i in 0 until totalCells / 7) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (j in 0 until 7) {
                    val dayIndex = i * 7 + j
                    if (dayIndex >= firstDayOfWeekValue && dayIndex < firstDayOfWeekValue + daysInMonth) {
                        val dateNumber = dayIndex - firstDayOfWeekValue + 1
                        val currentDate = yearMonth.atDay(dateNumber)
                        DayCell(
                            date = currentDate,
                            today = today, // <- Truyền today vào
                            isSelected = selectedDates.contains(currentDate),
                            onDateSelected = onDateSelected
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f).aspectRatio(1f))
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
fun RowScope.DayCell(
    date: LocalDate,
    today: LocalDate,
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isPastDate = date.isBefore(today)
    val isToday = date.isEqual(today)
    val isSunday = date.dayOfWeek.value == 7

    val cutoffTime = LocalTime.of(14, 30)
    val isPastTodayCutoff = isToday && LocalTime.now().isAfter(cutoffTime)

    val cellColor = when {
        isSelected -> Color(0xFF26C6DA)
        isToday && isPastTodayCutoff -> Color.LightGray
        isToday -> Color.White
        isPastDate || isSunday -> Color.LightGray
        else -> SecondaryColor
    }

    val textColor = when {
        isSelected -> Color.White
        isPastDate || isSunday || isPastTodayCutoff -> Color.Gray
        isToday ->Color.Gray
        else -> Color.White
    }

    val borderColor = if (isToday && !isSelected && !isPastTodayCutoff) Color(0xFF26C6DA) else Color.Transparent
    val isClickable = !isPastDate && !isSunday && !isPastTodayCutoff
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cellColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = isClickable) { onDateSelected(date) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Bold,
                fontSize = 14.sp
            )
            if (isToday) {
                Text(
                    text = "Hôm nay",
                    color = Color.Gray ,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


@Composable
fun CalendarLegend() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LegendItem(color = SecondaryColor, text = "Ngày có thể đăng ký")
        LegendItem(color = Color.LightGray, text = "Ngày ngoài vùng đăng ký khám")
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
                .border(1.dp, Color.DarkGray, RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 14.sp)
    }
}

