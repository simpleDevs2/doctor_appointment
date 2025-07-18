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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.SearchBar
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.common.removeVietnameseAccents
import com.example.doctorappoint.data.Constant.ImageUrl.IMG_URL
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.DoctorInfo
import com.example.doctorappoint.model.ListDoctorsScheduleResponse
import com.example.doctorappoint.model.User
import com.example.doctorappoint.ui.theme.PrimaryColor
import java.text.NumberFormat
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchString by remember { mutableStateOf("") }
    val bookingViewModel : BookingViewModel = viewModel()

    val listOfDoctorsScheduleState by bookingViewModel.listOfDoctorsSchedule.collectAsState()
    var selectedDepartment by remember { mutableStateOf("Chuyên khoa") }
    var selectedGender by remember { mutableStateOf("Giới tính") }
    var showProfileUpdateDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current



    val title = "Danh sách bác sĩ"

    LaunchedEffect(Unit) {
        bookingViewModel.getListDoctorsSchedule()
    }

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

        SpacerHeight(8.dp)

        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchString = searchString,
            placeholder = "Tìm kiếm bác sĩ",
            onSearchStringChange = { searchString = it }
        )
        SpacerHeight(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            // Dropdown cho khoa
            DepartmentFilterDropdown(
                modifier = Modifier.weight(1f),
                selectedDepartment = selectedDepartment,
                onDepartmentSelected = { selectedDepartment = it },
                doctorsScheduleState = listOfDoctorsScheduleState
            )
            SpacerWidth(12.dp)
            // Dropdown cho giới tính
            GenderFilterDropdown(
                modifier = Modifier.weight(1f),
                selectedGender = selectedGender,
                onGenderSelected = { selectedGender = it }
            )
        }
        SpacerHeight(12.dp)
        when(listOfDoctorsScheduleState){
            is NetworkResponse.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment =  Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ){
                    CircularProgressIndicator(color = PrimaryColor)
                    SpacerHeight(16.dp)
                    Text(text = "Đang tải danh sách bác sĩ...", color = Color.Gray, textAlign = TextAlign.Center)
                }
            }
            is NetworkResponse.Success -> {
                val listOfDoctorsSchedule = (listOfDoctorsScheduleState as NetworkResponse.Success).data
                val doctors = listOfDoctorsSchedule.data


                val filteredDoctors = remember(doctors, searchString, selectedDepartment, selectedGender) {
                    doctors.filter { doctor ->
                        val matchesSearch = if (searchString.isBlank()) {
                            true
                        } else {

                            doctor.name.removeVietnameseAccents().lowercase().contains(searchString.removeVietnameseAccents().lowercase())
                        }

                        val matchesDepartment = if (selectedDepartment == "Chuyên khoa") {
                            true
                        } else {
                            doctor.department.equals(selectedDepartment, ignoreCase = true)
                        }

                        val matchesGender = if (selectedGender == "Giới tính") {
                            true
                        } else {
                            doctor.gender.equals(selectedGender, ignoreCase = true)
                        }

                        matchesSearch && matchesDepartment && matchesGender
                    }
                }

                if (filteredDoctors.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Không tìm thấy bác sĩ phù hợp.",
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(filteredDoctors) { doctor ->
                            DoctorCard(
                                doctor = doctor,
                                onClick = {
                                    val currentUser = LoginManager.getUser(context)
                                    if (isUserProfileComplete(currentUser)) {
                                        navController.navigate("doctor_daily_screen/${doctor.id}")
                                    } else {
                                        showProfileUpdateDialog = true
                                    }
                                }
                            )
                        }
                    }
                }
            }

            is NetworkResponse.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Có lỗi xảy ra khi tải danh sách bác sĩ",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    SpacerHeight(16.dp)
                    Button(
                        onClick = {bookingViewModel.refreshListDoctorsSchedule() },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0066CC)
                        )
                    ) {
                        Text("Thử lại")
                    }
                }
            }
        }


    }
    if (showProfileUpdateDialog) {
        Dialog(onDismissRequest = { showProfileUpdateDialog = false }) {
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
                            text = "Cập nhật thông tin",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Vui lòng cập nhật thông tin cá nhân trước khi đặt lịch",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showProfileUpdateDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            )
                        ) {
                            Text("Hủy", color = Color.Gray)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                navController.navigate("personalInfo")
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor
                            )
                        ) {
                            Text("Cập nhật", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
private fun isUserProfileComplete(user: User?): Boolean {
    if (user == null) return false

    val hasName = user.name?.trim()?.length ?: 0 >= 2
    val hasAddress = user.address?.trim()?.length ?: 0 >= 5
    val hasGender = user.gender?.let {
        it.equals("Nam", true) || it.equals("Nữ", true)
                || it.equals("Male", true) || it.equals("Female", true)
    } ?: false
    val hasBirthdate = user.birthdate?.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) ?: false

    return hasName && hasAddress && hasGender && hasBirthdate
}

@Composable
fun DoctorCard(doctor: DoctorInfo, onClick :()->Unit) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable{onClick()}
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),

        ) {
            AsyncImage(
                model = (IMG_URL + doctor.image),
                contentDescription = "Doctor ${doctor.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(140.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.Top)
            )
            SpacerWidth(16.dp)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctor.degree +" "+ doctor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
                SpacerHeight(12.dp)
                Text(
                    text = "Giới tính ${doctor.gender}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W400,
                    color = Color.Black
                )
                SpacerHeight(12.dp)
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.W400)) {
                            append("Chuyên khoa ")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(doctor.department.uppercase())
                        }
                    },
                    fontSize = 16.sp

                )
                SpacerHeight(12.dp)
                val formattedPrice = NumberFormat
                    .getCurrencyInstance(Locale("vi", "VN"))
                    .format(doctor.price)
                    .replace("VND", "₫")
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Color.Black, fontWeight = FontWeight.W400)) {
                            append("Giá khám ")
                        }
                        withStyle(style = SpanStyle( fontWeight = FontWeight.Bold)) {
                            append(formattedPrice)
                        }
                    },
                    fontSize = 16.sp
                )


            }
        }

    }



}

@Composable
fun DepartmentFilterDropdown(
    modifier: Modifier = Modifier,
    selectedDepartment: String,
    onDepartmentSelected: (String) -> Unit,
    doctorsScheduleState: NetworkResponse<*>,
) {
    var expanded by remember { mutableStateOf(false) }
    val departments = remember(doctorsScheduleState) {
        if (doctorsScheduleState is NetworkResponse.Success) {
            val allDepartments = (doctorsScheduleState.data as ListDoctorsScheduleResponse).data
                .map { it.department }
                .distinct()
                .sorted()
            listOf("Chuyên khoa") + allDepartments
        } else {
            listOf("Chuyên khoa")
        }
    }

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = selectedDepartment, maxLines = 1,fontSize = 16.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Chọn khoa")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.45f)
        ) {
            departments.forEach { department ->
                DropdownMenuItem(
                    text = { Text(department) },
                    onClick = {
                        onDepartmentSelected(department)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun GenderFilterDropdown(
    modifier: Modifier = Modifier,
    selectedGender: String,
    onGenderSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val genders = listOf("Giới tính", "Nam", "Nữ")

    Column(modifier = modifier) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = selectedGender, maxLines = 1, fontSize = 16.sp)
            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Chọn giới tính")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.45f)
        ) {
            genders.forEach { gender ->
                DropdownMenuItem(
                    text = { Text(gender) },
                    onClick = {
                        onGenderSelected(gender)
                        expanded = false
                    }
                )
            }
        }
    }
}

