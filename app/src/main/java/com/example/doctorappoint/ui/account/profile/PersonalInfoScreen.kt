package com.example.doctorappoint.ui.account.profile

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.UpdateProfile
import com.example.doctorappoint.model.User
import kotlinx.datetime.LocalDate
import network.chaintech.kmp_date_time_picker.ui.datepicker.WheelDatePickerView
import network.chaintech.kmp_date_time_picker.utils.DateTimePickerView
import network.chaintech.kmp_date_time_picker.utils.now

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    userState: MutableState<User?>,
    onProfileUpdated: () -> Unit = {}
) {

    val context = LocalContext.current
    val personalInfoViewModel: PersonalInfoViewModel = viewModel()
    val updateProfileState by personalInfoViewModel.updateProfileState.collectAsState()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        BackBtnAndTitle(
            title = "Thông tin cá nhân",
            onBackClick = { navController.popBackStack() }
        )
        ProfileHeader(userState.value)
        SpacerHeight(24.dp)


        val reloadKey = remember { mutableStateOf(0) }

        LaunchedEffect(updateProfileState) {
            if (updateProfileState is NetworkResponse.Success) {
                reloadKey.value++
            }
        }

        PersonalInfoForm(
            key = reloadKey.value,
            user = userState.value,
            onSave = { updateProfile ->
                val token = LoginManager.getToken(context)
                if(!token.isNullOrEmpty()){
                    personalInfoViewModel.updateProfile(token, updateProfile)
                }
            }
        )
    }


    LaunchedEffect(updateProfileState) {
        when (val state = updateProfileState) {
            is NetworkResponse.Success -> {

                LoginManager.saveLoginData(context, state.data.data, state.data.data.api_token)
                userState.value = state.data.data
                Toast.makeText(context, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                onProfileUpdated()
            }
            is NetworkResponse.Error -> {

            }
            else -> {}
        }
    }
}

@Composable
fun ProfileHeader(user: User?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(Color(0xFF007BFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Profile Picture",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        SpacerWidth(16.dp)
        Text(
            text = user?.name ?: "User",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoForm(
    key: Int = 0,
    user: User?,
    onSave: (UpdateProfile) -> Unit
) {
    val fullName = user?.name ?: ""
    val nameParts = fullName.split(" ")
    val firstName = nameParts.lastOrNull() ?: ""
    val lastNameAndMiddleName = nameParts.dropLast(1).joinToString(" ")
    val context = LocalContext.current


    val originalLastNameAndMiddleName = remember { lastNameAndMiddleName }
    val originalFirstName = remember { firstName }
    val originalGender = remember { user?.gender ?: "" }
    val originalAddress = remember { user?.address ?: "" }
    val originalBirthdate = remember { user?.birthdate ?: "" }

    var lastNameAndMiddleNameState by remember { mutableStateOf(lastNameAndMiddleName) }
    var firstNameState by remember { mutableStateOf(firstName) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedGender by remember { mutableStateOf(user?.gender ?: "") }
    var address by remember { mutableStateOf(user?.address ?: "") }

    // Error states for validation
    var lastNameError by remember { mutableStateOf("") }
    var firstNameError by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf("") }
    var birthdateError by remember { mutableStateOf("") }
    var genderError by remember { mutableStateOf("") }


    LaunchedEffect(user?.birthdate) {
        user?.birthdate?.let { birthdateStr ->
            try {

                val parts = birthdateStr.split("-")
                if (parts.size == 3) {
                    selectedDate = LocalDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
                }
            } catch (e: Exception) {
                Log.e("PersonalInfoScreen", "Error parsing birthdate: $birthdateStr", e)
            }
        }
    }

    val phoneNumber = user?.phone ?: ""
    val focusManager = LocalFocusManager.current


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phone Number
        LabeledInputField(
            label = "Số điện thoại",
            value = phoneNumber,
            onValueChange = {},
            placeholder = phoneNumber,
            isLocked = true
        )

        // Last Name and Middle Name
        LabeledInputField(
            label = "Họ và tên lót",
            value = lastNameAndMiddleNameState,
            onValueChange = {
                lastNameAndMiddleNameState = it
                // Clear error when user starts typing
                if (lastNameError.isNotEmpty()) {
                    lastNameError = ""
                }
                // Validate name format
                if (it.isNotEmpty() && it.any { char -> char.isDigit() }) {
                    lastNameError = "Họ và tên lót không được chứa số"
                }
            },
            placeholder = "Họ và tên đệm...",
            isLocked = false,
            isError = lastNameError.isNotEmpty(),
            onImeActionDone = { focusManager.clearFocus() }
        )

        // Display last name error
        if (lastNameError.isNotEmpty()) {
            Text(
                text = lastNameError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        // First Name
        LabeledInputField(
            label = "Tên",
            value = firstNameState,
            onValueChange = {
                firstNameState = it
                // Clear error when user starts typing
                if (firstNameError.isNotEmpty()) {
                    firstNameError = ""
                }
                // Validate name format
                if (it.isNotEmpty() && it.any { char -> char.isDigit() }) {
                    firstNameError = "Tên không được chứa số"
                }
            },
            placeholder = "Tên...",
            isLocked = false,
            isError = firstNameError.isNotEmpty(),
            onImeActionDone = { focusManager.clearFocus() }
        )

        // Display first name error
        if (firstNameError.isNotEmpty()) {
            Text(
                text = firstNameError,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
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
                options = listOf("Nam", "Nữ"),
                modifier = Modifier.weight(1f)
            )
        }

        // Email
        LabeledInputField(
            label = "Địa chỉ",
            value = address,
            onValueChange = { address = it },
            placeholder = "Địa chỉ...",
            isLocked = false,
            onImeActionDone = { focusManager.clearFocus() }
        )

        SpacerHeight(16.dp)
        fun hasChanges(): Boolean {
            val currentFullName = "$lastNameAndMiddleNameState $firstNameState"
            val originalFullName = "$originalLastNameAndMiddleName $originalFirstName"

            val currentBirthdate = selectedDate?.let { "${it.year}-${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')}" } ?: ""

            return currentFullName != originalFullName ||
                    selectedGender != originalGender ||
                    address != originalAddress ||
                    currentBirthdate != originalBirthdate
        }

        fun isValidName(name: String): Boolean{
            val regex = Regex("^[\\p{L}\\s]+$")
            return regex.matches(name)
        }

        Button(
            onClick = {
                // Validate fields
                var allFieldsValid = true

                if (lastNameAndMiddleNameState.isEmpty()) {
                    lastNameError = "Họ và tên lót không được để trống"
                    allFieldsValid = false
                }

                if (firstNameState.isEmpty()) {
                    firstNameError = "Tên không được để trống"
                    allFieldsValid = false
                }

                if (address.isEmpty()) {
                    addressError = "Địa chỉ không được để trống"
                    allFieldsValid = false
                }

                if (selectedDate == null) {
                    birthdateError = "Ngày sinh không được để trống"
                    allFieldsValid = false
                }

                if (selectedGender.isEmpty()) {
                    genderError = "Giới tính không được để trống"
                    allFieldsValid = false
                }

                if (!allFieldsValid) {
                    Toast.makeText(context, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                if (!hasChanges()) {
                    Toast.makeText(context, "Không có thay đổi nào để lưu", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val selectedDateString = selectedDate?.let { "${it.year}-${it.monthNumber.toString().padStart(2, '0')}-${it.dayOfMonth.toString().padStart(2, '0')}" } ?: ""
                val updateProfile = UpdateProfile(
                    address = address,
                    birthdate = selectedDateString,
                    gender = selectedGender,
                    name = "$lastNameAndMiddleNameState $firstNameState",
                    phone = phoneNumber
                )
                onSave(updateProfile)
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
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    imeAction: ImeAction = ImeAction.Done,
    onImeActionDone: (() -> Unit)? = null
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
            isError = isError,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onImeActionDone?.invoke()
                }
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isError) Color.Red else Color(0xFF007BFF),
                unfocusedBorderColor = if (isError) Color.Red else Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledBorderColor = Color.LightGray,
                disabledTextColor = Color.Gray,
                errorBorderColor = Color.Red
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

        Button(
            onClick = {
                Log.d("DatePickerField", "Date picker clicked, showing picker")
                showDatePicker = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (displayDate.isNotEmpty()) displayDate else placeholder,
                    color = if (displayDate.isNotEmpty()) Color.Black else Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Date",
                    tint = Color.Gray
                )
            }
        }
    }
    
    if (showDatePicker) {
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
                fontSize = 20.sp,
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
                Log.d("DatePickerField", "Date selected: $date")
                onDateSelected(date)
                showDatePicker = false
            },
            onDismiss = {
                Log.d("DatePickerField", "Date picker dismissed")
                showDatePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
                    .menuAnchor(),
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