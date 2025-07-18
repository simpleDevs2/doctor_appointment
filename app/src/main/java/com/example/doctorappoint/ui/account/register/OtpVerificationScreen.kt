package com.example.doctorappoint.ui.theme.user

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.PhoneNumberUtils
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.google.firebase.Firebase
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.delay

@Composable
fun OtpVerificationScreen(
    navController: NavHostController,
    phoneNumber: String,
    verificationID: String,
    modifier: Modifier = Modifier,
    onCompleteRegister: () -> Unit = {}
) {
    var timer by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    val otpLength = 6
    var otp by remember { mutableStateOf("") }
    val context = LocalContext.current
    val activity = context as? Activity




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
        BackBtnAndTitle(title= "Xác nhận OTP",
            onBackClick = {
                navController.popBackStack()
            }
        )
        SpacerHeight(18.dp)

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
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
                text = "Mã OTP đã được gửi tới số điện thoại\n ${PhoneNumberUtils.formatForDisplay(phoneNumber)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.W400,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = otp,
                onValueChange = {
                    if (it.length <= otpLength) otp = it
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                label = { Text("Mã OTP") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            PrimaryActionButton("Xác nhận", onClick = {
                if (otp.length == otpLength) {
                    if(verificationID.isNotEmpty()){
                        val credential = PhoneAuthProvider.getCredential(verificationID, otp)
                        Firebase.auth.signInWithCredential(credential)
                            .addOnCompleteListener { task->
                                if(task.isSuccessful){
                                    Toast.makeText(context, "Xác thực thanh cong", Toast.LENGTH_SHORT).show()
                                    navController.previousBackStackEntry?.savedStateHandle?.set("otpVerified", true)
                                    navController.previousBackStackEntry?.savedStateHandle?.set("verifiedPhoneNumber", phoneNumber)
                                    navController.popBackStack()
                                }else{
                                    Toast.makeText(context, "Xác thực thất bại", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
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