package com.example.doctorappoint.ui.account.register

import android.R.attr.onClick
import android.R.attr.text
import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.PhoneNumberUtils
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.ApiResponse
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext

@Composable
fun RegisterScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onOtpVerificationClick: (String, String) -> Unit = { _, _ -> }
) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var phoneExistsMessage by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    var showPhoneExistDialog by remember { mutableStateOf(false) }


    val otpVerified = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Boolean>("otpVerified") ?: false

    val registerViewModel : RegisterViewModel = viewModel()
    val registerState by registerViewModel.registerState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val phoneNumberExistsState by registerViewModel.isPhoneNumberExistsState.collectAsState()


    LaunchedEffect(key1 = otpVerified) {
        if (otpVerified) {
            val savedPhone = navController.currentBackStackEntry
                ?.savedStateHandle
                ?.get<String>("verifiedPhoneNumber")

            if (savedPhone != null) {
                phoneNumber = savedPhone
                navController.currentBackStackEntry?.savedStateHandle?.remove<String>("verifiedPhoneNumber")
            }
        }
    }

    LaunchedEffect(registerState) {
        when (registerState) {
            is NetworkResponse.Success -> {
                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                isLoading = false
                onLoginClick()
            }
            is NetworkResponse.Error -> {
                Toast.makeText(context, (registerState as NetworkResponse.Error).message, Toast.LENGTH_LONG).show()
                isLoading = false
            }
            is NetworkResponse.Loading -> {

            }
            else -> {}
        }
    }

    LaunchedEffect(phoneNumberExistsState) {
        when (phoneNumberExistsState) {
            is NetworkResponse.Success -> {
                val data = (phoneNumberExistsState as NetworkResponse.Success).data
                if (data.status) {
                    phoneExistsMessage = data.message
                    showPhoneExistDialog = true
                }
            }

            is NetworkResponse.Error -> {

            }

            else -> Unit // Loading hoặc Initial
        }
    }




    val callbacks = remember {
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                isLoading = false
                Firebase.auth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {

                        navController.currentBackStackEntry?.savedStateHandle?.set("otpVerified", true)
                        navController.currentBackStackEntry?.savedStateHandle?.set("verifiedPhoneNumber", phoneNumber)
                    } else {
                        Toast.makeText(context, "Xác thực tự động thất bại: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }

            override fun onVerificationFailed(e: FirebaseException) {
                isLoading = false

                phoneNumberError = "Không thể gửi mã OTP. Vui lòng thử lại."
            }

            override fun onCodeSent(
                verificationID: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                isLoading = false
                onOtpVerificationClick(phoneNumber, verificationID)
            }
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        BackBtnAndTitle(title= "Đăng ký",
            onBackClick = {
                navController.popBackStack()
            }
        )
        SpacerHeight(18.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Heart Hands Icon",
                    modifier = Modifier.size(48.dp)
                )
                SpacerWidth(16.dp)

                Text(
                    text = "Chào mừng đến với\nXYZ HCMC",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryColor
                )
            }
            Text(
                "Vui lòng nhập số điện thoại để tiếp tục",
                fontSize = 14.sp,
                color = Color.Gray
            )
            SpacerHeight(12.dp)



                Text(
                    text = "Số điện thoại",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = phoneNumber,
                    onValueChange = {
                        phoneNumber = it
                        if (phoneNumberError.isNotEmpty()) {
                            phoneNumberError = ""
                        }
                        if (it.isNotEmpty() && !PhoneNumberUtils.isValidVietnamesePhoneNumber(it)) {
                            phoneNumberError = context.getString(R.string.invalid_phone)
                        }
                    },
                    placeholder = { Text("Số điện thoại...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Phone Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    isError = phoneNumberError.isNotEmpty(),
                    enabled = !isLoading
                )

                if (phoneNumberError.isNotEmpty()) {
                    Text(
                        text = phoneNumberError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            val focusManager = LocalFocusManager.current
            if (otpVerified) {

                Text(
                    text = "Mật khẩu",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError.isNotEmpty()) passwordError = ""

                        if (confirmPasswordError.isNotEmpty() && confirmPassword.isNotEmpty() && confirmPassword != it) {
                            confirmPasswordError = ""
                        }
                    },
                    placeholder = { Text("Nhập mật khẩu...") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError.isNotEmpty(),
                    enabled = !isLoading
                )
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Text(
                    text = "Xác nhận mật khẩu",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (confirmPasswordError.isNotEmpty()) confirmPasswordError = ""
                    },
                    placeholder = { Text("Nhập lại mật khẩu...") },

                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()

                            if (validatePasswordAndConfirmPassword(password, confirmPassword, ::isPasswordValid,
                                    { msg -> passwordError = msg }, { msg -> confirmPasswordError = msg })) {
                                isLoading = true
                                registerViewModel.register(phoneNumber, password)
                            }
                        }
                    ),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff
                        val description = if (passwordVisible) "Hide confirm password" else "Show confirm password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    isError = confirmPasswordError.isNotEmpty(),
                    enabled = !isLoading
                )
                if (confirmPasswordError.isNotEmpty()) {
                    Text(
                        text = confirmPasswordError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            SpacerHeight(16.dp)
            PrimaryActionButton(
                text = if (otpVerified) "Hoàn tất đăng ký" else "Tiếp tục",
                onClick = {
                    if (!otpVerified) {
                        if (!PhoneNumberUtils.isValidVietnamesePhoneNumber(phoneNumber)) {
                            phoneNumberError = context.getString(R.string.invalid_phone)
                            return@PrimaryActionButton
                        }

                        isLoading = true
                        registerViewModel.viewModelScope.launch(Dispatchers.IO) {
                            val exists = registerViewModel.isPhoneNumberExists(phoneNumber)

                            if (exists) {
                                showPhoneExistDialog = true
                                isLoading = false
                                return@launch
                            }

                            val fullPhoneNumber = PhoneNumberUtils.toInternationalFormat(phoneNumber)
                            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                                .setPhoneNumber(fullPhoneNumber)
                                .setTimeout(60L, TimeUnit.SECONDS)
                                .setActivity(context as Activity)
                                .setCallbacks(callbacks)
                                .build()

                            PhoneAuthProvider.verifyPhoneNumber(options)
                            isLoading = false
                        }
                        return@PrimaryActionButton
                    }


                    if (!isPasswordValid(password)) {
                        passwordError = "Mật khẩu phải có ít nhất 8 ký tự, không chứa khoảng trắng và không bắt đầu bằng số."
                        return@PrimaryActionButton
                    }

                    isLoading = true
                    registerViewModel.register(phoneNumber, password)
                },
                enabled = !isLoading,
                isLoading = isLoading
            )

            SpacerHeight(16.dp)
            Button(
                onClick = onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .border(1.dp, PrimaryColor, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                enabled = !isLoading
            ) {
                Text("Đăng nhập", color = PrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }
        }
    }

    if (showPhoneExistDialog) {
        Dialog(onDismissRequest = { showPhoneExistDialog = false }) {
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
                            text = "Thông báo",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = phoneExistsMessage ,
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
                                showPhoneExistDialog = false
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
                               navController.navigate("login")

                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor
                            )
                        ) {
                            Text("Đăng nhập", color = Color.White)
                        }
                    }
                }
            }
        }
    }

}

val passwordRegex = Regex("^(?!\\d)[^\\s]{8,}$")

fun isPasswordValid(password: String): Boolean {
    return passwordRegex.matches(password)
}

fun validatePasswordAndConfirmPassword(
    password: String,
    confirmPassword: String,
    isPasswordValid: (String) -> Boolean,
    setPasswordError: (String) -> Unit,
    setConfirmPasswordError: (String) -> Unit
): Boolean {
    var isValid = true

    if (!isPasswordValid(password)) {
        setPasswordError("Mật khẩu phải có ít nhất 8 ký tự, không chứa khoảng trắng và không bắt đầu bằng số.")
        isValid = false
    } else {
        setPasswordError("")
    }

    if (confirmPassword.isEmpty()) {
        setConfirmPasswordError("Vui lòng xác nhận mật khẩu.")
        isValid = false
    } else if (password != confirmPassword) {
        setConfirmPasswordError("Mật khẩu xác nhận không khớp.")
        isValid = false
    } else {
        setConfirmPasswordError("")
    }

    return isValid
}