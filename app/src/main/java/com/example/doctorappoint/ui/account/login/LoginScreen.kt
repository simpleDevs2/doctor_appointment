package com.example.doctorappoint.ui.account.login

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doctorappoint.R
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.ui.theme.PrimaryColor

@SuppressLint("SuspiciousIndentation")
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onFogotPasswordClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
    ) {
    val context = LocalContext.current
    val loginViewModel: LoginViewModel = viewModel()

    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var keepSignedIn by remember { mutableStateOf(false) }

    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is NetworkResponse.Success -> {
                if (state.data.status) {
                    // Save login data
                    LoginManager.saveLoginData(context, state.data.user, state.data.token)
                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    // Navigate to home screen
                    onLoginClick()
                } else {
                    Toast.makeText(context, state.data.message, Toast.LENGTH_SHORT).show()
                }
            }
            is NetworkResponse.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
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
            val focusManager = LocalFocusManager.current
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
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next),
                    keyboardActions = KeyboardActions(
                        onNext = {
                            focusManager.moveFocus(FocusDirection.Down)
                        }
                    ),
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
                    onValueChange = {  password = it   },
                    placeholder = { Text("Nhập mật khẩu...") },
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.profile),
                            contentDescription = "Password Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            // Handle login
                            if(checkEmptyFields(context,phoneNumber,password)){
                                loginViewModel.login(phoneNumber, password)
                            }
                        }
                    ),
                    visualTransformation = PasswordVisualTransformation(),
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

                // Error message display
                if (loginState is NetworkResponse.Error) {
                    val errorMessage = (loginState as NetworkResponse.Error).message
                    if (errorMessage.contains("Incorrect phone number or password")) {
                        Text(
                            text = "Số điện thoại hoặc mật khẩu không đúng",
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                PrimaryActionButton(text = "Đăng nhập", onClick = {
                    if(checkEmptyFields(context,phoneNumber,password)){
                        loginViewModel.login(phoneNumber, password)
                    }
                })



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
                        .border(1.dp, PrimaryColor, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Đăng ký tài khoản mới", color = PrimaryColor,fontSize = 18.sp,fontWeight = FontWeight.W500)
                }
            }
        }
    }

private fun checkEmptyFields(context: Context,phoneNumber: String, password: String): Boolean {
    if(phoneNumber.isEmpty()){
        Toast.makeText(context, R.string.empty_phone, Toast.LENGTH_SHORT).show();
        return false;
    }

    if(phoneNumber.length != 10){
        Toast.makeText(context, R.string.invalid_phone, Toast.LENGTH_SHORT).show();
        return false;
    }
//    if(phoneNumber.startsWith("0")){
//        Toast.makeText(context, R.string.except_first_zero_number, Toast.LENGTH_SHORT).show();
//        return false;
//    }

    if(password.isEmpty()){
        Toast.makeText(context, R.string.empty_password, Toast.LENGTH_SHORT).show();
        return false;
    }

    return true
}

