package com.example.doctorappoint.ui.service

import ScheduleResponse
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Healing
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
import com.example.doctorappoint.common.formatDateForAPI
import com.example.doctorappoint.data.api.NetworkResponse

@Composable
fun BookingTimeScreen(
    navController: NavHostController,
    departmentId: Int,
    date: String,
    modifier: Modifier = Modifier
) {
    val bookingViewModel: BookingViewModel = viewModel()
    val doctorScheduleState by bookingViewModel.doctorSchedule.collectAsState()

    LaunchedEffect(departmentId, date) {
        bookingViewModel.getDoctorSchedule(departmentId, date)
    }

    val schedules = when (doctorScheduleState) {
        is NetworkResponse.Success -> (doctorScheduleState as NetworkResponse.Success<List<ScheduleResponse>>).data
        else -> emptyList()
    }
    val isLoading = doctorScheduleState is NetworkResponse.Loading

    val doctors = schedules.map { schedule ->
        DoctorTimeInfo(
            scheduleDetailId = schedule.doctors.firstOrNull()?.schedule_detail_id ?: 0,
            doctorId = schedule.doctors.firstOrNull()?.id?: 0,
            name = schedule.doctors.firstOrNull()?.name ?: "Bác sĩ",
            room = schedule.room.name,
            dates = listOf(formatDateForAPI(schedule.working_date)),
            selectedDate = formatDateForAPI(schedule.working_date),
            session = schedule.shift,
            sessionColor = when (schedule.shift.lowercase()) {
                "sáng", "morning" -> Color(0xFF7BC1B7)
                "chiều", "afternoon" -> Color(0xFF0B8FAC)
                else -> Color(0xFF1976D2)
            },
            timeSlots = parseTimeSlots(schedule.time)
        )
    }

    // state để lưu duy nhất 1 lựa chọn
    var selectedDoctor by remember { mutableStateOf<String?>(null) }
    var selectedSlot by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            title = "Chọn giờ khám",
            onBackClick = { navController.popBackStack() }
        )
        SpacerHeight(12.dp)

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            doctors.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Không có lịch bác sĩ cho ngày này",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            else -> {
                doctors.forEach { doctor ->
                    DoctorTimeCard(
                        doctor = doctor,
                        selectedDoctor = selectedDoctor,
                        selectedSlot = selectedSlot,
                        onSlotSelected = { doctorName, slotTime ->
                            selectedDoctor = doctorName
                            selectedSlot = slotTime
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("schedule_detail_id", doctor.scheduleDetailId)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_time", slotTime)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_doctor", doctorName)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("room",doctor.room )
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("doctor_id",doctor.doctorId )

                            navController.popBackStack()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun DoctorTimeCard(
    doctor: DoctorTimeInfo,
    selectedDoctor: String?,
    selectedSlot: String?,
    onSlotSelected: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Healing,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(24.dp)
                )
                SpacerWidth(8.dp)
                Text(
                    text = doctor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1976D2)
                )
            }
            SpacerHeight(4.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Roofing,
                    contentDescription = null,
                    tint = Color(0xFF757575),
                    modifier = Modifier.size(20.dp)
                )
                SpacerWidth(8.dp)
                Text(
                    text = doctor.room,
                    fontSize = 15.sp,
                    color = Color(0xFF757575)
                )
            }
            SpacerHeight(8.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${doctor.selectedDate} - ${doctor.session}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = doctor.sessionColor
                )
            }
            SpacerHeight(8.dp)

            val slots = doctor.timeSlots
            val rows = slots.chunked(2)
            rows.forEach { rowSlots ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    rowSlots.forEach { slot ->
                        val isSelected = (selectedDoctor == doctor.name && selectedSlot == slot.time && slot.enabled)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        !slot.enabled -> Color(0xFFE0E0E0)
                                        isSelected -> Color(0xFF1976D2)
                                        else -> Color.White
                                    }
                                )
                                .border(
                                    width = 2.dp,
                                    color = when {
                                        !slot.enabled -> Color(0xFFE0E0E0)
                                        isSelected -> Color(0xFF1976D2)
                                        else -> Color(0xFF1976D2)
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = slot.enabled) {
                                    onSlotSelected(doctor.name, slot.time)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = slot.time,
                                color = when {
                                    !slot.enabled -> Color.Gray
                                    isSelected -> Color.White
                                    else -> Color(0xFF1976D2)
                                },
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    if (rowSlots.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


fun parseTimeSlots(timeRange: String): List<TimeSlot> {
    val parts = timeRange.split("-")
    if (parts.size != 2) return emptyList()
    val start = parts[0].substringBefore(":").toIntOrNull() ?: return emptyList()
    val end = parts[1].substringBefore(":").toIntOrNull() ?: return emptyList()
    return (start until end).map { hour ->
        val from = "%02d:00".format(hour)
        val to = "%02d:00".format(hour + 1)
        TimeSlot("$from - $to", true)
    }
}

data class DoctorTimeInfo(
   val scheduleDetailId :Int,
   val doctorId :Int,
    val name: String,
    val room: String,
    val dates: List<String>,
    val selectedDate: String,
    val session: String,
    val sessionColor: Color,
    val timeSlots: List<TimeSlot>
)

data class TimeSlot(
    val time: String,
    val enabled: Boolean
)
