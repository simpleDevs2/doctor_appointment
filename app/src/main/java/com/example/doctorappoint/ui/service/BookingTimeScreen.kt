package com.example.doctorappoint.ui.service

import ScheduleTimeSlot
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
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.ui.theme.PrimaryColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

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
           bookingViewModel.getDoctorSchedule(departmentId,date)
    }

    val isLoading = doctorScheduleState is NetworkResponse.Loading
    val errorMessage = (doctorScheduleState as? NetworkResponse.Error)?.message

    val doctors:List<DoctorTimeInfo> = remember(doctorScheduleState){
        when (val state = doctorScheduleState) {
            is NetworkResponse.Success ->{
                state.data.flatten().flatMap { scheduleResponse->
                    scheduleResponse.doctors.map { doctor ->
                        DoctorTimeInfo(
                            scheduleDetailId = doctor.schedule_detail_id,
                            doctorId = doctor.id,
                            name = doctor.name,
                            room = scheduleResponse.room.name,
                            selectedDate = scheduleResponse.working_date,
                            session = scheduleResponse.shift,
                            sessionColor = when (scheduleResponse.shift.lowercase()) {
                                "ca sáng", "sáng", "morning" -> Color(0xFF0B8FAC)
                                "ca chiều", "chiều", "afternoon" -> Color(0xFF0B8FAC)
                                else -> Color(0xFF1976D2)
                            },
                            timeSlots = doctor.slots
                        )
                    }
                }
            }
            else -> emptyList()
        }
    }

    var selectedDoctorName by remember { mutableStateOf<String?>(null) }
    var selectedSlotStartTime by remember { mutableStateOf<String?>(null) }

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
                    CircularProgressIndicator(color = PrimaryColor)
                }
            }
            errorMessage != null -> {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lỗi: $errorMessage",
                    fontSize = 16.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold
                )
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
                        selectedDoctorName = selectedDoctorName,
                        selectedSlotTime = selectedSlotStartTime,
                        onSlotSelected = { selectedDocInfo, selectedTimeSlot  ->
                            selectedDoctorName  = selectedDocInfo.name
                            selectedSlotStartTime   = selectedTimeSlot.start_time
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("schedule_detail_id", selectedDocInfo.scheduleDetailId)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_time", selectedTimeSlot.start_time)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("selected_doctor", selectedDocInfo.name)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("room", selectedDocInfo.room)
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("doctor_id", selectedDocInfo.doctorId)
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
    selectedDoctorName: String?,
    selectedSlotTime: String?,
    onSlotSelected: (DoctorTimeInfo, ScheduleTimeSlot) -> Unit
) {
    val currentDate = LocalDate.now()
    val currentLocalTime = LocalTime.now()

    val selectedLocalDate = remember(doctor.selectedDate) {
        LocalDate.parse(doctor.selectedDate, DateTimeFormatter.ISO_LOCAL_DATE)
    }
    // check ngay hien tai
    val isSelectedDateToday = selectedLocalDate.isEqual(currentDate)

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
                        val slotStartTime = LocalTime.parse(slot.start_time)
                        val MAX_BOOKED_SLOTS = 4
                        // check full lich
                        val isFullyBooked = slot.booked_slots_count >= MAX_BOOKED_SLOTS
                        // check da troi qua gio dat chua
                        val isPastTime = isSelectedDateToday && slotStartTime.isBefore(currentLocalTime)

                        val isEnabled = !isFullyBooked && !isPastTime
                        val isSelected = (selectedDoctorName == doctor.name && selectedSlotTime == slot.start_time && isEnabled)



                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        !isEnabled -> Color(0xFFE0E0E0)
                                        isSelected -> PrimaryColor
                                        else -> Color.White
                                    }
                                )
                                .border(
                                    width = 2.dp,
                                    color = when {
                                        !isEnabled -> Color(0xFFE0E0E0)
                                        isSelected -> PrimaryColor
                                        else ->  PrimaryColor
                                    },
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(enabled = isEnabled) {
                                    onSlotSelected(doctor, slot)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${slot.start_time}-${slot.end_time}",
                                color = when {
                                    !isEnabled -> Color.Gray
                                    isSelected -> Color.White
                                    else ->  PrimaryColor
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

data class DoctorTimeInfo(
    val scheduleDetailId: Int,
    val doctorId: Int,
    val name: String,
    val room: String,
    val selectedDate: String,
    val session: String,
    val sessionColor: Color,
    val timeSlots: List<ScheduleTimeSlot>
)

