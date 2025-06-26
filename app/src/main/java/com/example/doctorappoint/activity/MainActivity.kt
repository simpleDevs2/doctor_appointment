package com.example.doctorappoint.activity


import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doctorappoint.navigation.HomeScreen
import com.example.doctorappoint.navigation.MainScreen

import com.example.doctorappoint.navigation.WelcomeScreen
import com.example.doctorappoint.ui.account.profile.PersonalInfoScreen
import com.example.doctorappoint.ui.service.AppointmentDatePicker
import com.example.doctorappoint.ui.theme.DoctorAppointTheme
import com.example.doctorappoint.ui.theme.service.DoctorListScreen
import com.example.doctorappoint.ui.theme.service.SelectDepartmentScreen
import com.example.doctorappoint.ui.theme.user.LoginScreen
import com.example.doctorappoint.ui.theme.user.RegisterScreen


class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DoctorAppointTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PersonalInfoScreen(modifier = Modifier.padding(innerPadding))
                }

            }
           //MyApp()
        }
    }

}

@Composable
fun MyApp(){
   val navController = rememberNavController()
    DoctorAppointTheme {
        NavHost(navController = navController, startDestination = "welcome" ){
            composable("welcome"){
            WelcomeScreen(
                onLoginClick = { navController.navigate("login") },
                onRegisterClick = { navController.navigate("register") }
            )
            }
            //user
            composable("login"){
               LoginScreen(
                   onLoginClick = {
                       navController.navigate("main"){
                           popUpTo("login"){
                               inclusive = true
                           }
                       }
                   },
                   onFogotPasswordClick = { },
                   onRegisterClick = { navController.navigate("register") }
               )
            }
            composable("register"){
                RegisterScreen(
                    onLoginClick = { navController.navigate("login") },
                    onRegisterClick = {  }
                )
            }
            composable("main"){
                MainScreen(navController)
            }
            composable("selectDepartment") {
                SelectDepartmentScreen(navController = navController)
            }
            composable("doctorList") {
                DoctorListScreen(navController = navController)
            }
        }
    }
}



