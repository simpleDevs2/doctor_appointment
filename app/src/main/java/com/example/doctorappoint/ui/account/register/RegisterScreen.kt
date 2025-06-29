package com.example.doctorappoint.ui.theme.user

import android.app.Activity
import android.widget.Toast
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.PhoneNumberUtils
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.ui.account.register.OtpViewModel
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
    val otpViewModel: OtpViewModel = viewModel()
    
    val otpState by otpViewModel.otpState.collectAsState()
    val verificationId by otpViewModel.verificationId.collectAsState()

    LaunchedEffect(otpState) {
        when (val state = otpState) {
            is NetworkResponse.Success -> {
                Toast.makeText(context, "Mã OTP đã được gửi", Toast.LENGTH_SHORT).show()
                // Navigate to OTP verification screen with the verificationId from ViewModel
                verificationId?.let { id ->
                    onOtpVerificationClick(phoneNumber, id)
                }
            }
            is NetworkResponse.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            is NetworkResponse.Loading -> {
                // Loading state - no action needed
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 48.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        SpacerHeight(24.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
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
                onValueChange = { phoneNumber = it },
                placeholder = { Text("Số điện thoại...") },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Phone Icon",
                        modifier = Modifier.size(28.dp)
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            SpacerHeight(16.dp)

            PrimaryActionButton(
                text = "Đăng ký", 
                onClick = {
                    
                    if (!PhoneNumberUtils.isValidVietnamesePhoneNumber(phoneNumber)) {
                        Toast.makeText(context, "Số điện thoại không hợp lệ. Vui lòng nhập số điện thoại gồm 10 chữ số bắt đầu bằng số 0.", Toast.LENGTH_LONG).show()
                        return@PrimaryActionButton
                    }

                    if (activity != null) {
                        // Format for SMS using utility
                        val fullPhoneNumber = PhoneNumberUtils.toInternationalFormat(phoneNumber)
                        otpViewModel.sendOtp(fullPhoneNumber, activity)
                    } else {
                        Toast.makeText(context, "Lỗi ứng dụng: Không thể lấy Activity.", Toast.LENGTH_SHORT).show()
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