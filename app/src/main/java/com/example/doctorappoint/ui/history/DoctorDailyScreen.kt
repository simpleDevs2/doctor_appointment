package com.example.doctorappoint.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.DailyScheduleDetail
import com.example.doctorappoint.model.DoctorInfo
import com.example.doctorappoint.ui.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DoctorDailyScreen(
    navController: NavHostController,
    doctorId: Int,
    modifier: Modifier = Modifier
) {
    val historyViewModel: HistoryViewModel = viewModel()
    val doctorDailyScheduleState by historyViewModel.doctorDailyScheduleState.collectAsState()

    LaunchedEffect(doctorId) {
        historyViewModel.getDoctorDailySchedule(doctorId)
    }

    val isLoading = doctorDailyScheduleState is NetworkResponse.Loading
    val errorMessage = (doctorDailyScheduleState as? NetworkResponse.Error)?.message

    val doctorInfo = (doctorDailyScheduleState as? NetworkResponse.Success)?.data?.data?.doctor
    val allDailySchedules = (doctorDailyScheduleState as? NetworkResponse.Success)?.data?.data?.schedules ?: emptyList()

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }


    LaunchedEffect(allDailySchedules) {
        if (allDailySchedules.isNotEmpty() && selectedDate == null) {
            selectedDate = LocalDate.parse(allDailySchedules.first().working_date)
        }
    }

    val schedulesForSelectedDate = allDailySchedules.filter {
        selectedDate != null && LocalDate.parse(it.working_date) == selectedDate
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(title = "Chọn lịch khám") { navController.popBackStack() }
        SpacerHeight(24.dp)

        when {
            isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryColor)
            }
            errorMessage != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage, color = Color.Gray)
            }
            doctorInfo != null -> {
                UnifiedDoctorScheduleCard(
                    doctor = doctorInfo,
                    allDates = allDailySchedules.map { LocalDate.parse(it.working_date) }.distinct().sorted(),
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it },
                    scheduleDetails = schedulesForSelectedDate,
                    navController = navController
                )
            }
            else -> Text("Không có lịch khám", color = Color.Gray)
        }
    }
}

@Composable
fun UnifiedDoctorScheduleCard(
    doctor: DoctorInfo,
    allDates: List<LocalDate>,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    scheduleDetails: List<DailyScheduleDetail>,
    navController: NavHostController
) {
    val currentDate = LocalDate.now()
    val currentTime = LocalTime.now()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PersonOutline, contentDescription = null, tint = Color.Black)
                SpacerWidth(8.dp)
                Text("${doctor.degree} ${doctor.name.uppercase()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
            }
            SpacerHeight(8.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Roofing, contentDescription = null, tint = Color.Black)
                SpacerWidth(8.dp)
                Text(doctor.department, fontSize = 14.sp, color = Color.Black)
            }
            SpacerHeight(12.dp)

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allDates) { date ->
                    val selected = date == selectedDate
                    val dayMonth = date.format(DateTimeFormatter.ofPattern("dd/MM"))
                    val year = date.year.toString()
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onDateSelected(date) }
                            .background(if (selected) PrimaryColor else Color.White)
                            .border(1.dp, if (selected) PrimaryColor else Color.Gray, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .width(48.dp)
                            .height(56.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(dayMonth, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = if (selected) Color.White else Color.Black)
                        Text(year, fontSize = 12.sp, color = if (selected) Color.White else Color.Gray)
                    }
                }
            }

            SpacerHeight(16.dp)
            val shiftOrder = mapOf("Ca sáng" to 0, "Ca chiều" to 1)
            val sortedSchedules = scheduleDetails.sortedBy { shiftOrder[it.shift] ?: Int.MAX_VALUE }

            sortedSchedules.forEach { schedule ->
                Text("${schedule.room} - ${schedule.shift} (${LocalDate.parse(schedule.working_date).dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("vi"))})",
                    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF388E3C))
                SpacerHeight(8.dp)

                schedule.slots.chunked(2).forEach { rowSlots ->
                    Row( horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)) {
                        rowSlots.forEach { slot ->
                            val startTime = LocalTime.parse(slot.start_time)
                            val isPast = LocalDate.parse(schedule.working_date) == currentDate && startTime.isBefore(currentTime)
                            val isFull = slot.booked_slots_count >= 4
                            val enabled = !isPast && !isFull

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (enabled) Color.White else Color.LightGray)
                                    .border(2.dp, if (enabled) PrimaryColor else Color.Gray, RoundedCornerShape(8.dp))
                                    .clickable(enabled = enabled) {
                                        val bookingData = mapOf(
                                            "scheduleDetailId" to schedule.schedule_detail_id,
                                            "departmentName" to doctor.department,
                                            "selectedDate" to schedule.working_date,
                                            "selectedTime" to slot.start_time,
                                            "selectedRoom" to schedule.room,
                                            "price" to doctor.price,
                                            "doctorId" to doctor.id,
                                            "doctorName" to doctor.name
                                        )
                                        navController.currentBackStackEntry?.savedStateHandle?.set("bookingData", bookingData)
                                        navController.navigate("booking_summary")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "${slot.start_time} - ${slot.end_time}",
                                    color = if (enabled) PrimaryColor else Color.DarkGray,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        if (rowSlots.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}