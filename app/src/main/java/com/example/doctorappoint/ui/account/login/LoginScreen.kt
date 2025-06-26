package com.example.doctorappoint.ui.theme.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.R
import com.example.doctorappoint.component.PrimaryActionButton
import com.example.doctorappoint.component.SpacerHeight
import com.example.doctorappoint.component.SpacerWidth
import com.example.doctorappoint.ui.theme.PrimaryColor

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onFogotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
    ) {
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepSignedIn by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding( vertical = 48.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {

        SpacerHeight(24.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background), // Thay thế bằng icon của bạn
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
                    "Vui lòng đăng nhập để tiếp tục",
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
                        Icon(painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Phone Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Mật khẩu",
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("Nhập mật khẩu...") },
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Password Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = keepSignedIn,
                        onCheckedChange = { keepSignedIn = it },
                        colors = CheckboxDefaults.colors(checkedColor = PrimaryColor)
                    )
                    Text("Lưu thông tin đăng nhập")
                }

               PrimaryActionButton(text = "Đăng nhập", onClick = onLoginClick)


                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center){
                        TextButton(onClick = onFogotPasswordClick) {
                            Text(text = "Quên mật khẩu?", color = PrimaryColor)
                        }
                    }


                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryColor
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .border(1.dp, PrimaryColor, RoundedCornerShape(12.dp)), // Viền xanh nhạt
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp) // Không đổ bóng
                ) {
                    Text("Đăng ký tài khoản mới", color = PrimaryColor,fontSize = 18.sp,fontWeight = FontWeight.W500)
                }
            }
        }
    }

