package com.example.doctorappoint.ui.service

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.formatDateForAPI
import com.example.doctorappoint.ui.theme.PrimaryColor
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookingSummaryScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user = LoginManager.getUser(context)

    val previousEntry = navController.previousBackStackEntry
    val bookingData = previousEntry
        ?.savedStateHandle
        ?.get<Map<String, Any>>("bookingData")

    val scheduleDetailId = bookingData?.get("scheduleDetailId") as? Int ?: -1
    val departmentId = bookingData?.get("departmentId") as? Int ?: -1
    val departmentName = bookingData?.get("departmentName") as? String ?: ""
    val selectedDate = bookingData?.get("selectedDate") as? String ?: ""
    val selectedTime = bookingData?.get("selectedTime") as? String ?: ""
    val selectedRoom = bookingData?.get("selectedRoom") as? String ?: ""
    val price = bookingData?.get("price") as? Int ?: 0
    val doctorId = bookingData?.get("doctorId") as? Int ?: -1
    val doctorName = bookingData?.get("doctorName") as? String ?: ""

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            title = "Thông tin đặt lịch",
            onBackClick = { navController.popBackStack() }
        )
        SpacerHeight(18.dp)

        // Hồ sơ bệnh nhân
        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.PermIdentity,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Hồ sơ đăng ký khám bệnh",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                user?.let {
                    InfoRow("Họ tên", it.name.uppercase(),highlight = true)
                    InfoRow("Giới tính", it.gender)
                    InfoRow("Điện thoại", it.phone)
                    InfoRow("Địa chỉ", it.address)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Thông tin đặt lịch
        Text(
            text = "Thông tin lịch khám",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Bác sĩ", doctorName.uppercase(),highlight = true)
                InfoRow("Khoa", departmentName.uppercase(),highlight = true)
                InfoRow("Ngày khám", formatDateForAPI(selectedDate))
                InfoRow("Giờ khám", selectedTime)
                InfoRow("Phòng", selectedRoom)
                InfoRow("Giá", NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                    .format(price).replace("VND", "₫"))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Tổng tiền
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Tổng tiền khám",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            val formattedPrice = NumberFormat
                .getCurrencyInstance(Locale("vi", "VN"))
                .format(price)
                .replace("VND", "₫")
            Text(
                text = formattedPrice,
                fontSize = 16.sp,
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        PrimaryActionButton(
            text = "Đặt lịch",
            onClick = {
                val appointmentData = mapOf(
                    "scheduleDetailId" to scheduleDetailId,
                    "departmentName" to departmentName,
                    "selectedTime" to selectedTime,
                    "price" to price,
                )
                navController.currentBackStackEntry?.savedStateHandle?.set("appointmentData", appointmentData)
                navController.navigate("payment" )
            },
            modifier = Modifier.padding(bottom = 24.dp)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String, highlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Column(
            modifier = Modifier.weight(0.4f)
        ) {
            Text(text = "$label:", fontSize = 14.sp)
        }

        Column(
            modifier = Modifier.weight(0.6f)

        ) {
            Text(
                text = value,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Normal,
                color = if (highlight) PrimaryColor else Color.Black,
                fontSize = 14.sp
            )
        }


    }
}
