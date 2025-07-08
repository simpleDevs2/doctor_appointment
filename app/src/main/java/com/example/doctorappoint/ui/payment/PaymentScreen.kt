package com.example.doctorappoint.ui.payment

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.google.gson.Gson
import kotlinx.coroutines.launch
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener
import java.text.NumberFormat
import java.util.Locale


@Composable
fun PaymentScreen(
    navController: NavHostController
) {


    val context = LocalContext.current
    val activity  = context as? Activity
    val paymentViewModel : PaymentViewModel = viewModel()
    val uiState by paymentViewModel.uiState.collectAsState()
    val paymentState by paymentViewModel.appointmentState.collectAsState()
    var showCancelDialog by remember { mutableStateOf(false) }


    val previousEntry = navController.previousBackStackEntry
    val appointment = previousEntry
        ?.savedStateHandle
        ?.get<Map<String, Any>>("appointmentData")

    val scheduleDetailId = appointment?.get("scheduleDetailId") as? Int ?: -1
    val departmentName = appointment?.get("departmentName") as? String ?: ""
    val selectedTime = appointment?.get("selectedTime") as? String ?: ""
    val price = appointment?.get("price") as? Double ?: 0.0
    val formattedPrice = NumberFormat
        .getCurrencyInstance(Locale("vi", "VN"))
        .format(price)
        .replace("VND", "₫")

    val user = LoginManager.getUser(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            title = "Thanh toán",
            onBackClick = {
                showCancelDialog = true
            }
        )
        SpacerHeight(24.dp)
       Column(
           modifier = Modifier
               .fillMaxWidth()
               .background(Color.LightGray)
               .padding(8.dp)
       ) {
           Text(
               text = "Vui lòng kiểm tra thông tin đăng ký và chọn phương thức thanh toán.",
               fontSize = 14.sp,
               color =Color.Black
           )
           Spacer(Modifier.height(4.dp))
           Text(
               text = "Sau khi thanh toán thành công, bạn vui lòng đợi nhận PHIẾU KHÁM BỆNH, không đóng ứng dụng.",
               fontSize = 14.sp,
               color = PrimaryColor
           )
       }

        Spacer(Modifier.height(16.dp))


        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.stethoscope),
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    SpacerWidth(12.dp)
                    Text(
                        text = "Chuyên khoa đã chọn ",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = departmentName, fontWeight = FontWeight.Medium)
                    Text(text =formattedPrice , color = Color(0xFF1976D2), fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Payment,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    SpacerWidth(12.dp)
                    Text(
                        text = "Phương thức thanh toán",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* handle click */ }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.zalopay),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Ví điện tử ZaloPay", fontSize = 15.sp)
                    Spacer(Modifier.weight(1f))
                    RadioButton(
                        selected = true,
                        onClick = { /* handle click */ }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Tổng tiền khám
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Tổng tiền khám:", fontWeight = FontWeight.Bold)
            Text(
                text = formattedPrice,
                color = Color(0xFF43A047),
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.weight(1f))

        val timeToSend = selectedTime
            .split("-")
            .firstOrNull()
            ?.trim() ?: ""
        PrimaryActionButton(
            text = "Thanh toán",
            onClick = {
                Log.d("PaymentScreen", "Voi $scheduleDetailId va $selectedTime")
                Log.d("PaymentScreen", "Time $timeToSend")
                paymentViewModel.viewModelScope.launch {
                    val success = paymentViewModel.makeAppointment(
                        userId = user?.id?.toInt() ?: -1,
                        scheduleDetailId = scheduleDetailId,
                        appointmentTime = timeToSend
                    )
                    if (success) {
                        paymentViewModel.handleEvent(PaymentEvent.AmountChanged(price.toInt().toString()))
                        paymentViewModel.handleEvent(PaymentEvent.CreateOrder)
                        Log.d("PaymentScreen", "Appointment ok, creating order...")
                    } else {
                        Log.d("PaymentScreen", "Appointment failed, do not continue payment")
                        AlertDialog.Builder(activity)
                            .setTitle("Đặt lịch thất bại")
                            .setMessage("Không thể đặt lịch khám, vui lòng thử lại sau.")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }

                Log.d("PaymentScreen", "Thanh toán button clicked, creating order...")
            },
        )

    }

    if (uiState.showToken && activity != null) {
        LaunchedEffect(uiState.zpTransToken) {
            ZaloPaySDK.getInstance().payOrder(
                activity,
                uiState.zpTransToken,
                "demozpdk://app",
                object : PayOrderListener {
                    override fun onPaymentSucceeded(
                        transactionId: String,
                        transToken: String,
                        appTransID: String
                    ) {
                        Log.d(
                            "PaymentScreen",
                            "Payment succeeded: transactionId=$transactionId, transToken=$transToken")
                        paymentViewModel.handleEvent(PaymentEvent.ClearSuccess)
                        when (val currentState = paymentState) {
                            is NetworkResponse.Success -> {
                                val bookingData = currentState.data.data
                                Log.d("PaymentScreen", "Data response $bookingData")

                                val bookingMap = mapOf(
                                    "booking_id" to bookingData.booking_id,
                                    "appointment_date" to bookingData.appointment_date,
                                    "appointment_time" to bookingData.appointment_time,
                                    "doctor_name" to bookingData.doctor_name,
                                    "room_name" to bookingData.room_name,
                                    "shift" to bookingData.shift,
                                    "schedule_id" to bookingData.schedule_id,
                                    "doctor_id" to bookingData.doctor_id,
                                    "room_id" to bookingData.room_id,
                                    "schedule_detail_id" to bookingData.schedule_detail_id,
                                    "working_date" to bookingData.working_date
                                )

                                val bookingJson = Gson().toJson(bookingMap)

                                paymentViewModel.viewModelScope.launch {
                                    val userId = user?.id?.toInt() ?: -1
                                    val bookingId = bookingData.booking_id
                                    val confirmOk = paymentViewModel.confirmPayment(userId, bookingId, transactionId)
                                    if (confirmOk) {
                                        Log.d("PaymentScreen", "Confirm payment ok")
                                        navController.navigate(
                                            "payment_success/${transactionId}?bookingData=${
                                                Uri.encode(
                                                    bookingJson
                                                )
                                            }"
                                        ) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    }

                                }
                            }

                            is NetworkResponse.Error -> TODO()
                            NetworkResponse.Loading -> TODO()
                        }

                    }

                    override fun onPaymentCanceled(zpTransToken: String, appTransID: String) {
                        paymentViewModel.clearToken()
                        Log.d(
                            "PaymentScreen",
                            "Payment canceled: zpTransToken=$zpTransToken"
                        )
                        AlertDialog.Builder(activity)
                            .setTitle("Đã hủy")
                            .setMessage("Giao dịch đã bị hủy.")
                            .setPositiveButton("OK", null)
                            .show()
                    }

                    override fun onPaymentError(
                        zaloPayError: vn.zalopay.sdk.ZaloPayError,
                        zpTransToken: String,
                        appTransID: String
                    ) {
                        paymentViewModel.clearToken()
                        AlertDialog.Builder(activity)
                            .setTitle("Lỗi thanh toán")
                            .setMessage("Mã lỗi: ")
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            )
        }
    }
    if (showCancelDialog) {
        Dialog(onDismissRequest = { showCancelDialog = false }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Xác nhận",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn có muốn huỷ thanh toán và quay lại?",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showCancelDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                        ) {
                            Text("Hủy", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                showCancelDialog = false
                               navController.navigate("selectDepartment")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                        ) {
                            Text("Đồng ý", color = Color.White)
                        }
                    }
                }
            }
        }
    }

}
