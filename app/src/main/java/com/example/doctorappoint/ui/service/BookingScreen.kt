package com.example.doctorappoint.ui.theme.service


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PermIdentity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.doctorappoint.common.formatDateForAPI
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.Department
import com.example.doctorappoint.ui.service.BookingViewModel
import com.example.doctorappoint.ui.service.DepartmentViewModel


@Composable
fun BookingScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    departmentId: Int,
    price: Double
){
    val departmentViewModel: DepartmentViewModel = viewModel()
    val departmentsState by departmentViewModel.departments.collectAsState()
    val bookingViewModel: BookingViewModel = viewModel()

    val departmentName = when (departmentsState) {
        is NetworkResponse.Success -> {
            val departments = (departmentsState as NetworkResponse.Success<List<Department>>).data
            departments.find { it.id == departmentId }?.name ?: ""
        }
        else -> ""
    }

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

    val scheduleDoctorId = navBackStackEntry?.savedStateHandle?.getLiveData<Int>("schedule_detail_id")
    val selectedDate = navBackStackEntry?.savedStateHandle?.getLiveData<String>("selected_date")
    val selectedTime = navBackStackEntry?.savedStateHandle?.getLiveData<String>("selected_time")
    val selectedDoctor = navBackStackEntry?.savedStateHandle?.getLiveData<String>("selected_doctor")
    val room = navBackStackEntry?.savedStateHandle?.getLiveData<String>("room")
    val doctorId = navBackStackEntry?.savedStateHandle?.getLiveData<Int>("doctor_id")

    val isDepartmentSelected = departmentName.isNotBlank()
    val isDateSelected = !selectedDate?.value.isNullOrBlank()
            && formatDate(selectedDate?.value.toString()) != "Chọn ngày khám"
    val isTimeSelected = selectedTime?.value != null



    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            title = "Đặt lịch",
            onBackClick = { navController.popBackStack() }
        )
        SpacerHeight(24.dp)

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
            selectedValue = selectedTime?.value.orEmpty(),
            onRowClick = {
                if (isDepartmentSelected && isDateSelected) {
                    val dateForApi = formatDateForAPI(selectedDate.value ?: "")
                    navController.navigate("booking_time_screen/$departmentId/$dateForApi")
                }
            },
            icon = Icons.Default.AccessTime
        )
        SpacerHeight(32.dp)
        DateTimeSelectionRow(
            title = "Bác sĩ",
            selectedValue =if (selectedDoctor?.value!= null){
                selectedDoctor.value.toString()
            }else{
                "Chọn bác sĩ"
            },
            onRowClick = {

            },
            icon = Icons.Default.PermIdentity
        )
        Spacer(modifier = Modifier.weight(1f))
        PrimaryActionButton(
            text = "TIẾP TỤC",
            onClick = {
                val scheduleId = scheduleDoctorId?.value ?: -1
                val selDate = selectedDate?.value.orEmpty()
                val selTime = selectedTime?.value.orEmpty()
                val selDoctor = selectedDoctor?.value.orEmpty()
                val selRoom = room?.value.orEmpty()
                val selDoctorId = doctorId?.value ?: -1

                val bookingData = mapOf(
                    "scheduleDetailId" to scheduleId,
                    "departmentId" to departmentId,
                    "departmentName" to departmentName,
                    "selectedDate" to selDate,
                    "selectedTime" to selTime,
                    "selectedRoom" to selRoom,
                    "price" to price,
                    "doctorId" to selDoctorId,
                    "doctorName" to selDoctor
                )

                navController.currentBackStackEntry?.savedStateHandle?.set("bookingData", bookingData)
                navController.navigate("Booking_summary" )
            },
            modifier = Modifier.padding(bottom = 24.dp),
            enabled = isDepartmentSelected && isDateSelected && isTimeSelected
        )
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

