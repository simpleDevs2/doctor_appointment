package com.example.doctorappoint.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
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
import com.example.doctorappoint.ui.payment.PaymentScreen
import com.example.doctorappoint.ui.payment.PaymentSuccessScreen
import com.example.doctorappoint.ui.service.BookingDateScreen
import com.example.doctorappoint.ui.service.BookingSummaryScreen
import com.example.doctorappoint.ui.service.BookingTimeScreen
import com.example.doctorappoint.ui.theme.DoctorAppointTheme
import com.example.doctorappoint.ui.theme.service.BookingScreen
import com.example.doctorappoint.ui.theme.service.SelectDepartmentScreen
import com.example.doctorappoint.ui.theme.user.OtpVerificationScreen
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll()
            .build()
        StrictMode.setThreadPolicy(policy)

        ZaloPaySDK.init(2553, Environment.SANDBOX)

        setContent {

          MyApp()
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        ZaloPaySDK.getInstance().onResult(intent)
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
            composable("booking/{departmentId}/{price}"){ backStackEntry ->
                val departmentId = backStackEntry.arguments?.getString("departmentId")?.toIntOrNull()?:-1
                val price = backStackEntry.arguments?.getString("price")?.toDoubleOrNull()?: 0.0
                BookingScreen(
                    navController = navController,
                    departmentId = departmentId,
                    price = price
                )
            }
            composable("booking_date"){
                BookingDateScreen(navController = navController)
            }
            composable("booking_time_screen/{departmentId}/{date}") { backStackEntry ->
                val departmentId = backStackEntry.arguments?.getString("departmentId")?.toIntOrNull() ?: -1
                val date = backStackEntry.arguments?.getString("date") ?: ""
                BookingTimeScreen(navController = navController, departmentId = departmentId, date = date)
            }

            composable("Booking_summary") {
                BookingSummaryScreen(
                    navController = navController,
                )
            }
            composable("payment") {
                PaymentScreen(navController = navController)
            }

            composable(
                "payment_success/{transactionId}"
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                PaymentSuccessScreen(
                    transactionId = transactionId,
                    onBackToHome = { navController.popBackStack("main", false) }
                )
            }
        }
    }

}


