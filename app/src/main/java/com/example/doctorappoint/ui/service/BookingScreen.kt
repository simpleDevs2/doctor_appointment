package com.example.doctorappoint.ui.theme.service


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.model.Doctor
import com.example.doctorappoint.model.dummyDoctorList
import com.example.doctorappoint.ui.theme.BorderColor
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.example.doctorappoint.ui.theme.SecondaryColor
import com.example.doctorappoint.ui.theme.SelectedDateColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BookingScreen(modifier: Modifier = Modifier){
    val title = "Đặt lịch"
    val doctor =  dummyDoctorList.first()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }

    var showTimeSheet by remember { mutableStateOf(false) }
    var showDateSheet by remember { mutableStateOf(false) }

    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    var pickedTime by remember { mutableStateOf(LocalTime.NOON) }



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
        SpacerHeight(24.dp)
        DoctorDetailCard(doctor)
        SpacerHeight(32.dp)
        DateTimeSelectionRow(
            title = "Chuyên khoa",
            selectedValue = selectedTime,
            onRowClick = {  }
        )
        SpacerHeight(32.dp)
        DateTimeSelectionRow(
            title = "Ngày khám",
            selectedValue = selectedDate,
            onRowClick = { showDateSheet = true }
        )
        SpacerHeight(32.dp)
        DateTimeSelectionRow(
            title = "Giờ khám",
            selectedValue = selectedTime,
            onRowClick = {  showTimeSheet = true }
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryActionButton(
            text = "TIẾP TỤC",
            onClick = { },
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
    if (showTimeSheet) {
        TimePickerBottomSheet(
            onDismiss = { showTimeSheet = false },
            onTimeSelected = {
                selectedTime = it
                showTimeSheet = false
            }
        )
    }
    if (showDateSheet) {
        DatePickerBottomSheet(
            selectedDate =  selectedDate,
            onDismiss = { showDateSheet = false },
            onDateSelected = { selected ->
                selectedDate = selected.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            }
        )
    }
}

@Composable
fun DoctorDetailCard(doctor: Doctor, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = doctor.imageResId),
                contentDescription = "Doctor ${doctor.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
            SpacerWidth(16.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(top = 12.dp, bottom = 12.dp, end = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = doctor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Text(
                        text = " Giá khám: ",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1
                    )
                    Text(
                        text = doctor.price,
                        fontSize = 16.sp,
                        color = SecondaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

            }
        }
    }
}

@Composable
fun DateTimeSelectionRow(
    title: String,
    selectedValue: String,
    onRowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        SpacerHeight(8.dp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clickable(onClick = onRowClick), // Xử lý click
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Notifications, // Biểu tượng chuông
                        contentDescription = "Icon",
                        tint = Color.Gray
                    )
                    SpacerWidth(12.dp)
                    Text(
                        text = selectedValue.ifEmpty { "Chọn $title" }, // Hiển thị giá trị đã chọn hoặc placeholder
                        fontSize = 16.sp,
                        color = if (selectedValue.isEmpty()) Color.Gray else Color.Black
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight, // Biểu tượng mũi tên
                    contentDescription = "Arrow",
                    tint = Color.Gray
                )
            }
        }
    }
}

@Composable
fun TimeSlotButton(
    timeRange: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) SelectedDateColor else Color.White)
            .border(
                width = 1.dp,
                color = if (isSelected) SelectedDateColor else BorderColor,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = timeRange,
            color = if (isSelected) Color.White else Color.Black,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerBottomSheet(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val morningSlots = listOf("7h-8h", "8h-9h", "9h-10h", "10h-11h")
    val afternoonSlots = listOf("13h-14h", "14h-15h", "15h-16h")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text("Chọn giờ khám", fontSize = 18.sp, fontWeight = FontWeight.Bold)

            SpacerHeight(16.dp)
            Text("Ca sáng", fontWeight = FontWeight.SemiBold)
            SpacerHeight(8.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                morningSlots.forEach {
                    TimeSlotButton(timeRange = it, isSelected = false) {
                        onTimeSelected(it)
                    }
                }
            }

            SpacerHeight(16.dp)
            Text("Ca chiều", fontWeight = FontWeight.SemiBold)
            SpacerHeight(8.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                afternoonSlots.forEach {
                    TimeSlotButton(timeRange = it, isSelected = false) {
                        onTimeSelected(it)
                    }
                }
            }

            SpacerHeight(16.dp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DatePickerBottomSheet(
    selectedDate: String,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    val today = LocalDate.now()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "Select Date",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Header: Month and Year + arrows
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Previous Month",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        currentMonth = currentMonth.minusMonths(1)
                    }
                    .rotate(180f),
                tint = Color.Black
            )
            Text(
                text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Next Month",
                modifier = Modifier
                    .size(24.dp)
                    .clickable {
                        currentMonth = currentMonth.plusMonths(1)
                    },
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Day of Week header (Sunday start)
        val dayLabels = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEach { label ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = label, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }
            }
        }

        // Days Grid
        val daysInMonth = currentMonth.lengthOfMonth()
        val firstDayOfWeek = currentMonth.dayOfWeek.value % 7  // Sunday = 0

        var dayCounter = 1
        for (week in 0..5) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (dayOfWeek in 0..6) {
                    if (week == 0 && dayOfWeek < firstDayOfWeek || dayCounter > daysInMonth) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                        ) { }
                    } else {
                        val date = currentMonth.withDayOfMonth(dayCounter)
                        val isToday = date == today
                        val isPast = date.isBefore(today)

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    when {
                                        isPast -> Color.LightGray
                                        isToday -> PrimaryColor
                                        else -> Color.Transparent
                                    }
                                )
                                .clickable(enabled = !isPast) {
                                    onDateSelected(date)
                                    onDismiss()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayCounter.toString(),
                                color = when {
                                    isPast -> Color.Gray
                                    isToday -> Color.White
                                    else -> Color.Black
                                }
                            )
                        }
                        dayCounter++
                    }
                }
            }
        }
    }
}
