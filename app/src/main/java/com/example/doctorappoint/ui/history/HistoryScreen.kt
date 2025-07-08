// HistoryScreen.kt
package com.example.doctorappoint.ui.history

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.formatDateForAPI
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.UserHistory
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.google.gson.Gson

@Composable
fun HistoryScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val token = LoginManager.getToken(context) ?: ""
    val historyViewModel : HistoryViewModel = viewModel()
    val historyState by historyViewModel.historyState.collectAsState()
    var selectedFilter by remember { mutableStateOf("Đã thanh toán") }

    LaunchedEffect(Unit) {
        historyViewModel.getUserHistory(token)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(42.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val filters = listOf("Đã thanh toán", "Đã tiếp nhận", "Đã khám", "Đã hủy")
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Button(
                    onClick = { selectedFilter = filter },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =  Color.White ,
                        contentColor = if (isSelected) PrimaryColor else Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 4.dp)
                        .then(if (isSelected) Modifier.drawBehind {
                            val strokeWidth = 2.dp.toPx()
                            val y = size.height - strokeWidth / 2
                            drawLine(
                                color = PrimaryColor,
                                start = Offset(0f, y),
                                end = Offset(size.width, y),
                                strokeWidth = strokeWidth
                            )
                        } else Modifier)
                ) {
                    Text(filter, fontSize = 16.sp)
                }
            }
        }

        SpacerHeight(24.dp)

        when (historyState) {
            is NetworkResponse.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = PrimaryColor)
                    SpacerHeight(12.dp)
                    Text("Đang tải dữ liệu...", color = Color.Gray)
                }
            }

            is NetworkResponse.Error -> {
                val message = (historyState as NetworkResponse.Error).message
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Lỗi: $message", color = Color.Red)
                    SpacerHeight(12.dp)
                    Button(onClick = { historyViewModel.getUserHistory(token) }) {
                        Text("Thử lại")
                    }
                }
            }

            is NetworkResponse.Success -> {
                val allItems = (historyState as NetworkResponse.Success).data.data

                val filteredItems = allItems.filter { it.status == selectedFilter }

                LazyColumn(modifier = Modifier.padding(top=4.dp, bottom = 4.dp),verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        items(filteredItems) { item ->
                            HistoryItemCard(item, navController)
                        }
                }
            }

        }
    }
}

@Composable
fun HistoryItemCard(item: UserHistory,navController: NavHostController) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable{
                val historyDataMap = mapOf(
                    "appointment_date" to item.appointment_date,
                    "appointment_time" to item.appointment_time,
                    "department" to item.department,
                    "doctor" to item.doctor,
                    "payment_amount" to item.payment_amount,
                    "payment_status" to item.payment_status,
                    "room" to item.room,
                    "schedule_detail_id" to item.schedule_detail_id,
                    "status" to item.status
                )
                val historyJson = Gson().toJson(historyDataMap)
                navController.navigate(
                    "history_detail/?historyData=${
                        Uri.encode(
                            historyJson
                        )
                    }"
                )

            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Khoa: ${item.department.uppercase()}",
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = item.status,
                    color = Color(0xFF388E3C),
                    fontWeight = FontWeight.Medium
                )
            }
            SpacerHeight(8.dp)
            Text("Bác sĩ: ${item.doctor}", fontSize = 16.sp)
            val date = item.appointment_date
            Text("Thời gian: ${item.appointment_time} ${formatDateForAPI(date)}", fontSize = 16.sp)
            Text("Phòng: ${item.room}", fontSize = 16.sp)
        }
    }
}
