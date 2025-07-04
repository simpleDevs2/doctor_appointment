package com.example.doctorappoint.ui.service

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Roofing
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.SpacerWidth

@Composable
fun BookingTimeScreen(
    navController: NavHostController,
    selectedDate: String,
    modifier: Modifier = Modifier
) {
    // Dữ liệu ảo
    val doctors = listOf(
        DoctorTimeInfo(
            name = "BSCKII. Huỳnh Quốc Bảo",
            room = "Phòng 66 - Lầu 1 Khu B - Buổi sáng",
            dates = listOf("04/07/2025", "11/07/2025", "18/07/2025", "25/07/2025", "01/08/2025"),
            selectedDate = "04/07/2025",
            session = "Buổi sáng (Thứ 6)",
            sessionColor = Color(0xFF4CAF50),
            timeSlots = listOf(
                TimeSlot("06:30 - 07:30", false),
                TimeSlot("07:30 - 08:30", true),
                TimeSlot("08:30 - 09:30", true),
                TimeSlot("09:30 - 10:30", true),
                TimeSlot("10:30 - 11:30", false)
            )
        ),
        DoctorTimeInfo(
            name = "ThS BS. Nguyễn Ngọc Thôi",
            room = "Phòng 66 - Lầu 1 Khu B - Buổi chiều",
            dates = listOf("04/07/2025", "11/07/2025", "18/07/2025", "25/07/2025", "01/08/2025"),
            selectedDate = "04/07/2025",
            session = "Buổi chiều (Thứ 6)",
            sessionColor = Color(0xFFFF9800),
            timeSlots = listOf(
                TimeSlot("13:30 - 14:30", true),
                TimeSlot("14:30 - 15:30", true),
                TimeSlot("15:30 - 16:00", true)
            )
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
       BackBtnAndTitle(
          title =  "Chọn giờ khám",
           onBackClick = {
               navController.popBackStack()
           }
       )
        Spacer(modifier = Modifier.height(8.dp))
        doctors.forEach { doctor ->
            DoctorTimeCard(doctor)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun DoctorTimeCard(doctor: DoctorTimeInfo) {
    var selectedDate by remember { mutableStateOf(doctor.selectedDate) }
    var selectedSlot by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF1976D2),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Roofing,
                    contentDescription = null,
                    tint = Color(0xFF1976D2),
                    modifier = Modifier.size(20.dp)
                )
                SpacerWidth(8.dp)
                Text(
                    text = doctor.room,
                    fontSize = 15.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Dãy ngày
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                doctor.dates.forEach { date ->
                    val isSelected = date == selectedDate
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFFE3F0FF) else Color.White)
                            .border(
                                width = 2.dp,
                                color = if (isSelected) Color(0xFF1976D2) else Color(0xFFE0E0E0),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedDate = date },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = date.substring(0, 5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) Color(0xFF1976D2) else Color.Black
                            )
                            Text(
                                text = date.substring(6),
                                fontSize = 13.sp,
                                color = if (isSelected) Color(0xFF1976D2) else Color.Black
                            )
                        }
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.TopEnd).size(16.dp)
                                    .border(1.dp, Color.White, RoundedCornerShape(42)).padding(2.dp)
                                    .background( Color(0xFF1976D2) )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Thông tin ca
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    text = "$selectedDate - ${doctor.session}",
                    fontSize = 15.sp,
                    color = doctor.sessionColor,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Các khung giờ
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
                        val isSelected = selectedSlot == slot.time && slot.enabled
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
                                .clickable(enabled = slot.enabled) { selectedSlot = slot.time },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = slot.time,
                                color = when {
                                    !slot.enabled -> Color.Gray
                                    isSelected -> Color.White
                                    else -> Color(0xFF1976D2)
                                },
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }
                    // Nếu hàng này chỉ có 1 slot, thêm Spacer để căn trái
                    if (rowSlots.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

data class DoctorTimeInfo(
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