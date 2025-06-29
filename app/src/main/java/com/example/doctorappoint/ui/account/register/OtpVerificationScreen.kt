package com.example.doctorappoint.ui.theme.user

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackButton
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.common.PhoneNumberUtils
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.ui.account.register.OtpViewModel
import com.example.doctorappoint.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    navController: NavHostController,
    phoneNumber: String,
    verificationId: String,
    modifier: Modifier = Modifier,
    onHomeClick: () -> Unit = {}
) {
    var timer by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    val otpLength = 6
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? Activity
    val otpViewModel: OtpViewModel = viewModel()
    
    val verificationState by otpViewModel.verificationState.collectAsState()

    // Set the verification ID in the ViewModel when the screen is created
    LaunchedEffect(verificationId) {
        if (verificationId.isNotEmpty() && verificationId != "placeholder") {
            // Set the verification ID in the ViewModel for verification
            otpViewModel.setVerificationId(verificationId)
        }
    }

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000)
            timer--
        } else {
            canResend = true
        }
    }

    LaunchedEffect(verificationState) {
        when (val state = verificationState) {
            is NetworkResponse.Success -> {
                if (state.data) {
                    Toast.makeText(context, "Xác thực OTP thành công!", Toast.LENGTH_SHORT).show()
                    // Clear states before navigation to prevent memory leaks
                    otpViewModel.clearStates()
                    onHomeClick()
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
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        BackButton { navController.popBackStack() }
        SpacerHeight(24.dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background),
                    contentDescription = "Logo",
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
                text = "Nhập mã OTP",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                textAlign = TextAlign.Start
            )

            Text(
                text = "Mã OTP đã được gửi tới số điện thoại ${PhoneNumberUtils.formatForDisplay(phoneNumber)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            BasicTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= otpLength) otp = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                decorationBox = { innerTextField ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(otpLength) { index ->
                            val char = otp.getOrNull(index)?.toString() ?: ""
                            val isFocused = otp.length == index
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(
                                        width = 2.dp,
                                        color = if (isFocused) PrimaryColor else Color(0xFFE5E7EB),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (char.isNotEmpty()) Color.Black else Color.LightGray
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier.size(0.dp)
                    ) {
                        innerTextField()
                    }
                }
            )

            PrimaryActionButton("Xác nhận", onClick = {
                if (otp.length == otpLength) {
                    otpViewModel.verifyOtp(otp)
                } else {
                    Toast.makeText(context, "Vui lòng nhập đủ mã OTP.", Toast.LENGTH_SHORT).show()
                }
            })

            // Resend OTP section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (canResend) {
                        TextButton(
                            onClick = {
                                if (activity != null) {
                                    // Format for SMS using utility
                                    val fullPhoneNumber = PhoneNumberUtils.toInternationalFormat(phoneNumber)
                                    otpViewModel.resendOtp(fullPhoneNumber, activity)
                                    timer = 60
                                    canResend = false
                                }
                            }
                        ) {
                            Text(
                                text = "Gửi lại mã OTP",
                                color = PrimaryColor,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Text(
                            text = "Gửi lại mã trong ${timer}s",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}