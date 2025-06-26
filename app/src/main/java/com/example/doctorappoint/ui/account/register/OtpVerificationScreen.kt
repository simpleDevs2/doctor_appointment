package com.example.doctorappoint.ui.theme.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.R
import com.example.doctorappoint.component.BackButton
import com.example.doctorappoint.component.PrimaryActionButton
import com.example.doctorappoint.component.SpacerHeight
import com.example.doctorappoint.component.SpacerWidth
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.example.doctorappoint.ui.theme.SecondaryLight
import kotlinx.coroutines.delay


@Composable
fun OtpVerificationScreen(modifier: Modifier = Modifier) {
    var timer by remember { mutableStateOf(23) }
    var canResend by remember { mutableStateOf(false) }
    val otpLength = 6
    var otpValue by remember { mutableStateOf(TextFieldValue("")) }
    val focusRequesters = remember { List(otpLength) { FocusRequester() } }

    LaunchedEffect(key1 = timer) {
        if (timer > 0) {
            delay(1000)
            timer--
        } else {
            canResend = true
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
        BackButton{Unit}
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
                textAlign = TextAlign.Start // Căn trái
            )

            Text(
                text = "Nhập mã OTP được gửi qua số điện thoại của bạn",
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )


            BasicTextField(
                value = otpValue,
                onValueChange = { newValue ->
                    if (newValue.text.length <= otpLength && newValue.text.all { it.isDigit() }) {
                        otpValue = newValue
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp), // Chiều cao của TextField ẩn
                decorationBox = { innerTextField ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        repeat(otpLength) { index ->
                            val char = otpValue.text.getOrElse(index) { ' ' } // Lấy ký tự hoặc khoảng trắng
                            val isFocused = otpValue.selection.start == index && otpValue.selection.end == index
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .border(
                                        width = 2.dp,
                                        color = if (isFocused) PrimaryColor else Color(0xFFE5E7EB), // Highlight khi focus
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (char != ' ') Color.Black else Color.LightGray // Màu chữ khi có input
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

            // Verify button
            PrimaryActionButton("Xác nhận", onClick = { /* Handle verify button click */ })

            // Resend OTP section
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Gửi lại mã trong ${timer}s",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    SpacerHeight(4.dp)
                    TextButton(
                        onClick = {
                            if (canResend) {
                                timer = 23 // Reset timer
                                canResend = false
                                // TODO: Gửi lại mã OTP ở đây
                            }
                        },
                        enabled = canResend
                    ) {
                        Text(
                            text = "Gửi mã OTP",
                            color = if (canResend) PrimaryColor else SecondaryLight,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

