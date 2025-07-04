package com.example.doctorappoint.ui

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.navigation.WelcomeScreen
import com.example.doctorappoint.ui.account.login.LoginScreen
import com.example.doctorappoint.ui.account.profile.PersonalInfoScreen
import com.example.doctorappoint.ui.account.register.RegisterScreen
import com.example.doctorappoint.ui.home.MainScreen
import com.example.doctorappoint.ui.service.BookingDateScreen
import com.example.doctorappoint.ui.service.BookingTimeScreen
import com.example.doctorappoint.ui.theme.DoctorAppointTheme
import com.example.doctorappoint.ui.theme.service.BookingScreen
import com.example.doctorappoint.ui.theme.service.DoctorListScreen
import com.example.doctorappoint.ui.theme.service.SelectDepartmentScreen
import com.example.doctorappoint.ui.theme.user.OtpVerificationScreen

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApp()
        }
    }


}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    val context = navController.context

    var isLoggedIn by remember { mutableStateOf(LoginManager.isLoggedIn(context)) }


    DoctorAppointTheme {
        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) "main" else "welcome"
        ) {
            composable("welcome") {
                WelcomeScreen(
                    onLoginClick = { navController.navigate("login") },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            // Login flow
            composable("login") {
                LoginScreen(
                    navController = navController,
                    onLoginClick = {
                        isLoggedIn = true
                        navController.navigate("main") {
                            popUpTo("welcome") { inclusive = true }
                        }
                    },
                    onFogotPasswordClick = { },
                    onRegisterClick = { navController.navigate("register") }
                )
            }

            composable("register") {
                RegisterScreen(
                    navController = navController,
                    onLoginClick = { navController.navigate("login") },
                    onOtpVerificationClick = { phoneNumber, verificationId ->
                        navController.navigate("otpVerification/$phoneNumber/$verificationId")
                    }
                )
            }

            composable("otpVerification/{phoneNumber}/{verificationId}") { backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber")
                val verificationId = backStackEntry.arguments?.getString("verificationId")

                if (phoneNumber != null && verificationId != null) {
                    OtpVerificationScreen(
                        navController = navController,
                        phoneNumber = phoneNumber,
                        verificationId = verificationId,
                        onHomeClick = {
                            isLoggedIn = true
                            navController.navigate("main") {
                                popUpTo("welcome") { inclusive = true }
                            }
                        }
                    )
                } else {
                    navController.popBackStack()
                }
            }

            // Main app screens (only accessible when logged in)
            composable("main") {
                MainScreen(
                    navController = navController,
                    onLogout = {
                        navController.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable("personalInfo") {
                val user = LoginManager.getUser(context)
                val userState = remember { mutableStateOf(user) }
                PersonalInfoScreen(
                    navController = navController,
                    userState = userState
                )
            }

            composable("selectDepartment") {
                SelectDepartmentScreen(navController = navController)
            }
            composable("booking/{departmentId}"){ backStackEntry ->
                val departmentId = backStackEntry.arguments?.getString("departmentId")?.toIntOrNull()?:-1
                BookingScreen(
                    navController = navController,
                    departmentId = departmentId
                )
            }
            composable("booking_date"){
                BookingDateScreen(navController = navController)
            }
            composable(
                "booking_time_screen/{selectedDate}"
            ) { backStackEntry ->
                val selectedDate = backStackEntry.arguments?.getString("selectedDate") ?: ""
                BookingTimeScreen(navController, selectedDate = selectedDate)
            }

            composable("doctorList") {
                DoctorListScreen(navController = navController)
            }
        }
    }
}


