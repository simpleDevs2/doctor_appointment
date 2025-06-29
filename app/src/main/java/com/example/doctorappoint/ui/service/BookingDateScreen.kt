package com.example.doctorappoint.ui.service

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.SpacerHeight
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*


@Composable
fun AppointmentDatePicker(modifier: Modifier = Modifier) {
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
            .padding(horizontal = 16.dp)
    ) {
        BackBtnAndTitle(
            modifier,
            title,
            onBackClick = {
                // navController.popBackStack()
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
                selectedDates = if (selectedDates.contains(date)) {
                    selectedDates - date
                } else {
                    selectedDates + date
                }
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
                    color = Color.Gray
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
    today: LocalDate, // <- Nhận today
    isSelected: Boolean,
    onDateSelected: (LocalDate) -> Unit
) {
    val isPastDate = date.isBefore(today)
    val isToday = date.isEqual(today)

    // *** BẮT ĐẦU THAY ĐỔI LOGIC TẠI ĐÂY ***
    val cellColor = when {
        isSelected -> Color(0xFF26C6DA) // Màu đã chọn
        isPastDate -> Color.LightGray // Màu ngày quá khứ, như trong LegendItem
        else -> Color(0xFFF0F0F0)     // Màu nền mặc định cho ngày có thể chọn
    }

    val textColor = when {
        isSelected -> Color.White
        isPastDate -> Color.Gray // Màu chữ cho ngày quá khứ để dễ đọc
        else -> Color.Black
    }
    // *** KẾT THÚC THAY ĐỔI LOGIC ***

    val borderColor = if (isToday && !isSelected) Color(0xFF26C6DA) else Color.Transparent

    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(cellColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = !isPastDate) { onDateSelected(date) }, // Vô hiệu hóa click ngày quá khứ
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                color = textColor,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                fontSize = 14.sp
            )
            if (isToday) {
                Text(
                    text = "Hôm nay",
                    color = if (isSelected) Color.White else Color(0xFF26C6DA),
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
        LegendItem(color = Color(0xFF26C6DA), text = "Ngày đã chọn")
        LegendItem(color = Color(0xFFF0F0F0), text = "Ngày có thể đăng ký")
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

