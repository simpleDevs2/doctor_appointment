package com.example.doctorappoint.ui.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.doctorappoint.ui.account.profile.AccountScreen
import com.example.doctorappoint.ui.history.HistoryScreen
import com.example.doctorappoint.ui.theme.PrimaryColor

@Composable
fun MainScreen(
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val bottomBarNavController = rememberNavController()
    val navBackStackEntry by bottomBarNavController.currentBackStackEntryAsState()
    val context = LocalContext.current
    var doubleBackToExitPressedOnce  by remember { mutableStateOf(false) }
    val activity = context as? Activity

    BackHandler(enabled = true) {
        if (doubleBackToExitPressedOnce) {
            activity?.finish()
        } else {
            doubleBackToExitPressedOnce = true
            Toast.makeText(context, "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(doubleBackToExitPressedOnce) {
        if (doubleBackToExitPressedOnce) {
            kotlinx.coroutines.delay(2000L)
            doubleBackToExitPressedOnce = false
        }
    }


    data class BottomNavigationItem(
        val title: String,
        val route: String,
        val unselectedIcon: ImageVector,
        val selectedIcon: ImageVector
    )
    
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            route = "home",
            unselectedIcon = Icons.Default.Home,
            selectedIcon = Icons.Default.Home
        ),
        BottomNavigationItem(
            title = "History",
            route = "history",
            unselectedIcon = Icons.Default.History,
            selectedIcon = Icons.Default.History
        ),
        BottomNavigationItem(
            title = "Notification",
            route = "notification",
            unselectedIcon = Icons.Default.Notifications,
            selectedIcon = Icons.Default.Notifications
        ),
        BottomNavigationItem(
            title = "Profile",
            route = "profile",
            unselectedIcon = Icons.Default.Person,
            selectedIcon = Icons.Default.Person
        ),
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(72.dp),
                containerColor = Color.White
            ) {
                items.forEach { item ->
                    val isSelected = item.route == navBackStackEntry?.destination?.route
                    NavigationBarItem(
                        selected = isSelected,
                        icon = {
                            Icon(
                                imageVector = if (isSelected) {
                                    item.selectedIcon
                                } else item.unselectedIcon,
                                contentDescription = item.title
                            )
                        },
                        onClick = {
                            bottomBarNavController.navigate(item.route) {
                                popUpTo(bottomBarNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = PrimaryColor
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomBarNavController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(navController = navController)
            }
            composable("history") {
                HistoryScreen(navController = navController)
            }
            composable("notification") {

                Text("Notification Screen Content")
            }
            composable("profile") {
                AccountScreen(
                    navController = navController,
                    onLogout = onLogout
                )
            }
        }
    }
}

