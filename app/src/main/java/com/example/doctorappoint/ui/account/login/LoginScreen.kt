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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
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
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.PhoneNumberUtils
import com.example.doctorappoint.common.PrimaryActionButton
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.ui.theme.PrimaryColor

@SuppressLint("SuspiciousIndentation")
@Composable
fun LoginScreen(
    navController: NavHostController,
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
    var phoneNumberError by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }

    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is NetworkResponse.Success -> {
                if (state.data.status) {
                    LoginManager.saveLoginData(context, state.data.user, state.data.token)
                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    onLoginClick()
                } else {

                    loginError = state.data.message
                }
            }
            is NetworkResponse.Error -> {

                loginError = state.message
            }
            is NetworkResponse.Loading -> {

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

        BackBtnAndTitle(title= "Đăng nhập",
            onBackClick = {
                navController.popBackStack()
            }
        )
        SpacerHeight(18.dp)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ){


                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Thay thế bằng icon của bạn
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
                    onValueChange = {
                        phoneNumber = it

                        if (phoneNumberError.isNotEmpty()) {
                            phoneNumberError = ""
                        }
                        if (loginError.isNotEmpty()) {
                            loginError = ""
                        }

                        if (it.isNotEmpty() && !PhoneNumberUtils.isValidVietnamesePhoneNumber(it)) {
                            phoneNumberError = context.getString(R.string.invalid_phone)
                        }
                    },
                    placeholder = { Text("Số điện thoại...") },
                    leadingIcon = {
                        Icon (
                            imageVector = Icons.Default.Phone,
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
                        if (loginError.isNotEmpty()) {
                            loginError = ""
                        }
                    },
                    placeholder = { Text("Nhập mật khẩu...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
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
                            if(checkEmptyFields(context, phoneNumber, password) { error -> phoneNumberError = error }){
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

                // Login error message display
                if (loginError.isNotEmpty()) {
                    Text(
                        text = loginError,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                PrimaryActionButton(text = "Đăng nhập", onClick = {
                    if(checkEmptyFields(context, phoneNumber, password) { error -> phoneNumberError = error }){
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

private fun checkEmptyFields(context: Context, phoneNumber: String, password: String, onPhoneError: (String) -> Unit): Boolean {
    if (phoneNumber.isEmpty()) {
        onPhoneError(context.getString(R.string.empty_phone))
        return false
    }


    if (!PhoneNumberUtils.isValidVietnamesePhoneNumber(phoneNumber)) {
        onPhoneError(context.getString(R.string.invalid_phone))
        return false
    }

    if (password.isEmpty()) {
        Toast.makeText(context, R.string.empty_password, Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}

