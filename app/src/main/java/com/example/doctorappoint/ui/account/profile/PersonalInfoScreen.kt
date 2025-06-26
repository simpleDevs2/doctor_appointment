package com.example.doctorappoint.ui.account.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.component.SpacerHeight
import com.example.doctorappoint.component.SpacerWidth
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.now


@Composable
fun PersonalInfoScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(Color.White)
            .padding(16.dp)
    ) {
        ProfileHeader()
        SpacerHeight(24.dp)
        PersonalInfoForm()
    }
}

@Composable
fun ProfileHeader() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF007BFF)), // Example color
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        SpacerWidth(16.dp) // Make sure SpacerWidth is defined or imported
        Text(
            text = "094****574", // This could also come from user data state
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@Composable
fun PersonalInfoForm() {

    var lastNameAndMiddleName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedGender by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val phoneNumber = "094****574"

    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phone Number
        LabeledInputField(
            label = "Số điện thoại",
            value = phoneNumber, // Display value directly
            onValueChange = {},    // No change if locked
            placeholder = "094****574",
            isLocked = true
        )

        // Last Name and Middle Name
        LabeledInputField(
            label = "Họ và tên lót",
            value = lastNameAndMiddleName,
            onValueChange = { lastNameAndMiddleName = it },
            placeholder = "Họ và tên đệm...",
            isLocked = false
        )

        // First Name
        LabeledInputField(
            label = "Tên",
            value = firstName,
            onValueChange = { firstName = it },
            placeholder = "Tên...",
            isLocked = false
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top // Align items to the top for consistent label alignment
        ) {
            // Date of Birth
            DatePickerField(
                label = "Ngày sinh",
                selectedDate = selectedDate,
                onDateSelected = { selectedDate = it },
                placeholder = "DD/MM/YYYY",
                modifier = Modifier.weight(1f)
            )

            // Gender
            GenderSelectionField(
                label = "Giới tính",
                selectedGender = selectedGender,
                onGenderSelected = { selectedGender = it },
                options = listOf("Nam", "Nữ",),
                modifier = Modifier.weight(1f)
            )
        }

        // Email
        LabeledInputField(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            placeholder = "Email...",
            isLocked = false
        )

        SpacerHeight(16.dp)

        Button(
            onClick = {
                // Handle save action with the states:
                // lastNameAndMiddleName, firstName, selectedDate, selectedGender, email
                // Example: Log them or send to a ViewModel
                println("Saving Data: Name: $firstName $lastNameAndMiddleName, DOB: $selectedDate, Gender: $selectedGender, Email: $email")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Lưu thay đổi")
        }
    }
}

@Composable
fun LabeledInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isLocked: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = { if (!isLocked) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = isLocked,
            placeholder = { Text(placeholder) },
            trailingIcon = {
                if (isLocked) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF007BFF), // Example focus color
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray
            )
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

    val displayDate = selectedDate?.let {
        // Ensure day and month are two digits
        val day = it.dayOfMonth.toString().padStart(2, '0')
        val month = it.monthNumber.toString().padStart(2, '0')
        "$day/$month/${it.year}"
    } ?: ""

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = displayDate,
            onValueChange = { /* Not directly changed here */ },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    Log.e("test", "Clicked! Setting showDatePicker to true.")
                    showDatePicker = true
                },
            readOnly = true,
            placeholder = { Text(if (displayDate.isEmpty()) placeholder else "") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Date",
                    tint = Color.Gray
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF007BFF),
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )
    }
        if(showDatePicker){
            WheelDatePickerView(
                startDate = selectedDate ?: LocalDate.now(),
                title = "Chọn ngày sinh",
                doneLabel = "OK",
                showDatePicker = showDatePicker,
                height = 200.dp,
                dateTimePickerView = DateTimePickerView.BOTTOM_SHEET_VIEW,
                rowCount = 3,
                titleStyle = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp, // Adjusted size
                    fontWeight = FontWeight.Bold
                ),
                doneLabelStyle = TextStyle(
                    color = Color(0xFF007BFF),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                ),
                customMonthNames = listOf(
                    "Thg 1", "Thg 2", "Thg 3", "Thg 4",
                    "Thg 5", "Thg 6", "Thg 7", "Thg 8",
                    "Thg 9", "Thg 10", "Thg 11", "Thg 12"
                ),
                yearsRange = 1920..LocalDate.now().year,
                onDoneClick = { date ->
                    onDateSelected(date)
                    showDatePicker = false
                },
                onDismiss = {
                    showDatePicker = false
                }
            )
        }




}

@OptIn(ExperimentalMaterial3Api::class) // For ExposedDropdownMenuBox
@Composable
fun GenderSelectionField(
    label: String,
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    options: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedGender,
                onValueChange = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(), // Important for ExposedDropdownMenuBox
                readOnly = true,
                placeholder = { Text("Chọn") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF007BFF),
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                options.forEach { gender ->
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
}