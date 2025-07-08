package com.example.doctorappoint.ui.account.register

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.PhoneNumberUtils
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.ui.theme.PrimaryColor

@Composable
fun RegisterScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onOtpVerificationClick: (String, String) -> Unit = { _, _ -> }
) {
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? Activity
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var phoneNumberError by remember { mutableStateOf("") }
    var otpError by remember { mutableStateOf("") }
//    val otpState by otpViewModel.otpState.collectAsState()
//    val verificationId by otpViewModel.verificationId.collectAsState()
//
//    LaunchedEffect(otpState) {
//        when (val state = otpState) {
//            is NetworkResponse.Success -> {
//                Toast.makeText(context, "Mã OTP đã được gửi", Toast.LENGTH_SHORT).show()
//                // Navigate to OTP verification screen with the verificationId from ViewModel
//                verificationId?.let { id ->
//                    onOtpVerificationClick(phoneNumber, id)
//                }
//            }
//            is NetworkResponse.Error -> {
//                otpError = state.message
//            }
//            is NetworkResponse.Loading -> {
//                // Loading state - no action needed
//            }
//        }
//    }
    val otpVerified = navController.currentBackStackEntry
        ?.savedStateHandle
        ?.get<Boolean>("otpVerified") ?: false

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
                    if (otpError.isNotEmpty()) {
                        otpError = ""
                    }

                    if (it.isNotEmpty() && !PhoneNumberUtils.isValidVietnamesePhoneNumber(it)) {
                        phoneNumberError =  context.getString(R.string.invalid_phone)
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
                isError = phoneNumberError.isNotEmpty()
            )

            if (phoneNumberError.isNotEmpty()) {
                Text(
                    text = phoneNumberError,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

            // Display OTP error
            if (otpError.isNotEmpty()) {
                Text(
                    text = otpError,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }

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
                    },
                    placeholder = { Text("Nhập mật khẩu...") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError.isNotEmpty()
                )
                if (passwordError.isNotEmpty()) {
                    Text(
                        text = passwordError,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
            SpacerHeight(16.dp)

            PrimaryActionButton(
                text = "Đăng ký",
                onClick = {
                    if (!PhoneNumberUtils.isValidVietnamesePhoneNumber(phoneNumber)) {
                        phoneNumberError = R.string.invalid_phone.toString()
                        return@PrimaryActionButton
                    }
                    if (otpVerified) {
                        if (password.length < 8) {
                            passwordError = "Mật khẩu phải có ít nhất 8 ký tự"
                            return@PrimaryActionButton
                        }
                        // TODO: Gửi yêu cầu đăng ký thực tế tại đây
                    } else {
                        // bắt đầu gửi OTP
                        val fullPhoneNumber = PhoneNumberUtils.toInternationalFormat(phoneNumber)
                        // navigate sang OTP screen
                       // onOtpVerificationClick(fullPhoneNumber)
                    }

                }
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
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Đăng nhập", color = PrimaryColor, fontSize = 18.sp, fontWeight = FontWeight.W500)
            }
        }
    }
}