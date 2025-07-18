package com.example.doctorappoint.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.navigation.WelcomeScreen
import com.example.doctorappoint.ui.account.login.LoginScreen
import com.example.doctorappoint.ui.account.profile.PersonalInfoScreen
import com.example.doctorappoint.ui.account.register.RegisterScreen
import com.example.doctorappoint.ui.history.DoctorDailyScreen
import com.example.doctorappoint.ui.history.HistoryDetailScreen
import com.example.doctorappoint.ui.home.MainScreen
import com.example.doctorappoint.ui.payment.PaymentScreen
import com.example.doctorappoint.ui.payment.PaymentSuccessScreen
import com.example.doctorappoint.ui.service.BookingDateScreen
import com.example.doctorappoint.ui.service.BookingSummaryScreen
import com.example.doctorappoint.ui.service.BookingTimeScreen
import com.example.doctorappoint.ui.service.DoctorListScreen
import com.example.doctorappoint.ui.theme.DoctorAppointTheme
import com.example.doctorappoint.ui.theme.service.BookingScreen
import com.example.doctorappoint.ui.theme.service.SelectDepartmentScreen
import com.example.doctorappoint.ui.theme.user.OtpVerificationScreen
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPaySDK

class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance()
        )
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
                    onOtpVerificationClick = { phoneNumber, verificationID ->
                        navController.navigate("otpVerification/$phoneNumber/$verificationID")
                    }
                )
            }

            composable("otpVerification/{phoneNumber}/{verificationID}") { backStackEntry ->
                val phoneNumber = backStackEntry.arguments?.getString("phoneNumber")
                val verificationID = backStackEntry.arguments?.getString("verificationID")

                if (phoneNumber != null && verificationID != null) {
                    OtpVerificationScreen(
                        navController = navController,
                        phoneNumber = phoneNumber,
                        verificationID = verificationID,
                        onCompleteRegister ={
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
            composable("list_doctors_schedule"){
                DoctorListScreen(navController = navController)
            }

            composable(
                route = "booking/{departmentId}/{price}",
                arguments = listOf(
                    navArgument("departmentId") { type = NavType.IntType },
                    navArgument("price") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val departmentId = backStackEntry.arguments?.getInt("departmentId") ?: -1
                val price = backStackEntry.arguments?.getInt("price")?.toInt() ?: 0
                BookingScreen(
                    navController = navController,
                    departmentId = departmentId,
                    price = price
                )
            }
            composable("booking_date"){
                BookingDateScreen(navController = navController)
            }
            composable(
                route = "booking_time_screen/{departmentId}/{date}",
                arguments = listOf(
                    navArgument("departmentId") { type = NavType.IntType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val departmentId = backStackEntry.arguments?.getInt("departmentId") ?: -1
                val date = backStackEntry.arguments?.getString("date") ?: ""
                BookingTimeScreen(navController = navController, departmentId = departmentId, date = date)
            }

            composable("booking_summary") {
                BookingSummaryScreen(
                    navController = navController,
                )
            }
            composable("payment") {
                PaymentScreen(navController = navController)
            }
            composable(
                route = "payment_success/{transactionId}?bookingData={bookingData}",
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.StringType },
                    navArgument("bookingData") { type = NavType.StringType; nullable = true }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getString("transactionId") ?: ""
                val bookingDataJson = backStackEntry.arguments?.getString("bookingData")

                PaymentSuccessScreen(
                    navController = navController,
                    transactionId = transactionId,
                    bookingDataJson = bookingDataJson,
                    onBackToHome = {
                        navController.navigate("main") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    }
                )
            }
            composable("history_detail") {
                HistoryDetailScreen(navController = navController)
            }
            composable(
                route = "doctor_daily_screen/{doctorId}",
                arguments = listOf(
                    navArgument("doctorId") { type = NavType.IntType }
                )
            ){backstackEntry->
                val doctorId = backstackEntry.arguments?.getInt("doctorId")
                DoctorDailyScreen(
                    navController = navController,
                    doctorId = doctorId ?: -1
                )
            }
        }
    }

}


