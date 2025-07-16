package com.example.doctorappoint.ui.history


import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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

@Composable
fun HistoryDetailScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val user = LoginManager.getUser(context)

   val previousEntry = navController.previousBackStackEntry
    val historyData = previousEntry
        ?.savedStateHandle
        ?.get<Map<String,Any>>("historyData")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            title = "Chi tiết đặt lịch",
            onBackClick = { navController.popBackStack() }
        )
        SpacerHeight(18.dp)

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
                        text = "Thông tin bác sĩ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                historyData?.let {data->
                    InfoRow("Họ tên", data["doctor"].toString().uppercase(),highlight = true)

                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Thông tin đặt khám ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                historyData?.let {data->
                    InfoRow("Khoa", data["department"].toString().uppercase(), highlight = true)
                    InfoRow("Phòng", data["room"].toString())
                    InfoRow("Ngày khám", formatDateForAPI(data["appointment_date"].toString()))
                    InfoRow("Giờ khám", data["appointment_time"].toString())
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        val doctorId : Int = 11
        PrimaryActionButton(
            "Đặt lại",
            onClick = {
                navController.navigate("doctor_daily_screen/$doctorId"){
                    popUpTo("doctor_daily_screen/{doctorId}") {
                        inclusive = true
                    }
                    launchSingleTop = true
                }

            }

        )
        Spacer(modifier = Modifier.padding(bottom = 24.dp))
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
