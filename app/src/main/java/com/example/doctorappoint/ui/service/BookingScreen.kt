package com.example.doctorappoint.ui.theme.service


import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.Department
import com.example.doctorappoint.model.Doctor
import com.example.doctorappoint.model.dummyDoctorList
import com.example.doctorappoint.ui.service.DepartmentViewModel
import com.example.doctorappoint.ui.theme.BorderColor
import com.example.doctorappoint.ui.theme.SecondaryColor
import com.example.doctorappoint.ui.theme.SelectedDateColor


@Composable
fun BookingScreen(
    navController : NavHostController,
    modifier: Modifier = Modifier,
    departmentId :Int,

){
    val title = "Đặt lịch"
    val doctor =  dummyDoctorList.first()
    Log.d("dpId", "id = {$departmentId}")
    var selectedTime by remember { mutableStateOf("") }


    val  departmentViewModel: DepartmentViewModel = viewModel()
    val departmentsState by departmentViewModel.departments.collectAsState()

    val departmentName = when (departmentsState) {
        is NetworkResponse.Success -> {
            val departments = (departmentsState as NetworkResponse.Success<List<Department>>).data
            departments.find { it.id == departmentId }?.name ?: ""
        }
        else -> ""
    }

    Log.d("dpName", "name = {$departmentName}")
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            modifier,
            title,
            onBackClick = {
                navController.popBackStack()
            }
        )
        SpacerHeight(24.dp)
        DoctorDetailCard(doctor)
        SpacerHeight(32.dp)
        fun formatDate(dateString: String?): String {
            return if (dateString.isNullOrBlank()) {
                "Chọn ngày khám"
            } else {
                try {
                    val localDate = java.time.LocalDate.parse(dateString)
                    localDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                } catch (e: Exception) {
                    "Chọn ngày khám"
                }
            }
        }
        val navBackStackEntry = navController.currentBackStackEntryAsState().value
        val selectedDate = navBackStackEntry?.savedStateHandle?.getLiveData<String>("selected_date")
        val isDepartmentSelected = departmentName.isNotBlank()
        val isDateSelected = !selectedDate?.value.isNullOrBlank() && formatDate(selectedDate?.value.toString()) != "Chọn ngày khám"
        val isTimeSelected = selectedTime.isNotBlank()
        DateTimeSelectionRow(
            title = "Khoa:",
            selectedValue = departmentName,
            onRowClick = {
                    navController.navigate("selectDepartment")
            },
            icon = Icons.Default.HealthAndSafety
        )
        SpacerHeight(32.dp)

        DateTimeSelectionRow(
            title = "Ngày khám:",
            selectedValue = formatDate(selectedDate?.value.toString()),
            onRowClick = {
                if (isDepartmentSelected) {
                    navController.navigate("booking_date")
                }
            },
            icon = Icons.Default.CalendarToday
        )
        SpacerHeight(32.dp)
        DateTimeSelectionRow(
            title = "Giờ khám:",
            selectedValue = selectedTime,
            onRowClick = {
                if (isDepartmentSelected && isDateSelected) {
                    navController.navigate("booking_time_screen/${selectedDate?.value.toString()}")
                }
            },
            icon = Icons.Default.AccessTime
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryActionButton(
            text = "TIẾP TỤC",
            onClick = { },
            modifier = Modifier.padding(bottom = 24.dp),
            enabled = isDepartmentSelected && isDateSelected && isTimeSelected
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
    icon: ImageVector,
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
                        imageVector = icon,
                        contentDescription = "Icon",
                        tint = Color.Gray
                    )
                    SpacerWidth(12.dp)
                    Text(
                        text = selectedValue.ifEmpty { "Chọn $title" },
                        fontSize = 16.sp,
                        color = if (selectedValue.isEmpty()) Color.Gray else Color.Black
                    )
                }
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
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
