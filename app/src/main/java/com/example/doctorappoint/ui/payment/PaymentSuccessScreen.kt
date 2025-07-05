package com.example.doctorappoint.ui.payment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doctorappoint.R
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.AppointmentResponse
import com.example.doctorappoint.ui.service.InfoRow
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.example.doctorappoint.ui.theme.SecondaryColor

@Composable
fun PaymentSuccessScreen(
    transactionId: String,
    onBackToHome: () -> Unit
) {
    val paymentViewModel: PaymentViewModel = viewModel()
    val paymentState by paymentViewModel.appointmentState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp),
    ) {
        Spacer(modifier = Modifier.height(12.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Thông tin thanh toán",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = PrimaryColor
            )
            SpacerHeight(12.dp)
            HorizontalDivider(modifier = Modifier.fillMaxWidth(), thickness = 1.dp, color = Color.Gray)
            SpacerHeight(12.dp)
            Icon(
                painter = painterResource(id = R.drawable.check),
                contentDescription = null,
                tint = SecondaryColor,
                modifier = Modifier.size(80.dp)
            )
            SpacerHeight(12.dp)
            Text(
                text = "Thanh toán thành công",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,

            )
            SpacerHeight(8.dp)
            Text(
                text = "Mã giao dịch: $transactionId",
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val bookingData = when (paymentState) {
            is NetworkResponse.Success -> (paymentState as NetworkResponse.Success<AppointmentResponse>).data.data
            else -> null
        }

        Card(
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Mã đặt lịch", bookingData?.schedule_id.toString())
                InfoRow("Bác sĩ", bookingData?.doctor_name.toString())
                InfoRow("Ngày khám", bookingData?.appointment_date.toString())
                InfoRow("Giờ khám", bookingData?.appointment_time.toString())
                InfoRow("Phòng khám", bookingData?.room_name.toString())
                InfoRow("Ca khám", bookingData?.shift.toString())
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryActionButton(
            text = "Về trang chủ",
            onClick = {
               onBackToHome()
            }
        )
        SpacerHeight(12.dp)
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