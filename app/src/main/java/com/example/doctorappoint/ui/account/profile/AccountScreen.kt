package com.example.doctorappoint.ui.account.profile

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.ui.theme.PrimaryColor
import com.example.doctorappoint.ui.theme.SecondaryColor

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onLogout: () -> Unit
){
    val context = LocalContext.current
    var user by remember { mutableStateOf(LoginManager.getUser(context)) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        user = LoginManager.getUser(context)
    }
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp)
    ) {
        TopSection(user)
        SpacerHeight(16.dp)
        AccountOptions(
            onPersonalInfoClick = {
                navController.navigate("personalInfo")
            },
            onLogout = {
                showLogoutDialog = true
            }
        )
        SpacerHeight(16.dp)
    }

    if (showLogoutDialog) {
        Dialog(onDismissRequest = { showLogoutDialog = false }) {
            Box(
                modifier = Modifier
                    .width(300.dp)
                    .height(200.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Xác nhận đăng xuất",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Black
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                showLogoutDialog = false
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = SecondaryColor
                            )
                        ) {
                            Text("Hủy", color = Color.White)
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                showLogoutDialog = false
                                Toast.makeText(context, "Đã đăng xuất", Toast.LENGTH_SHORT).show()
                                onLogout()
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = PrimaryColor
                            )
                        ) {
                            Text("Đăng xuất", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopSection(user: com.example.doctorappoint.model.User?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFE0F7FA))
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            painter = painterResource(id = R.drawable.banner), // Replace with your actual logo resource
            contentDescription = "UMC CARE Logo",
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(8.dp)
        )
        SpacerHeight(8.dp)
        Text(
            text = "UMC CARE",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.DarkGray
        )
        SpacerHeight(8.dp)
        // User Name
        Text(
            text = user?.name ?: "User",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        SpacerHeight(4.dp)
        // Phone Number
        Text(
            text = user?.phone ?: "Phone",
            fontSize = 16.sp,
            fontWeight = FontWeight.Normal,
            color = Color.Gray
        )
    }
}

@Composable
fun AccountOptions(
    onPersonalInfoClick: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OptionItem(
            icon = Icons.Default.Person,
            text = "Thông tin cá nhân",
            onClick = onPersonalInfoClick
        )

        OptionItem(
            icon = Icons.Default.QuestionAnswer,
            text = "Hỗ trợ",
            onClick = { /* Handle click */ }
        )
        OptionItem(
            icon = Icons.Default.Info,
            text = "Điều khoản dịch vụ",
            onClick = { /* Handle click */ }
        )
        OptionItem(
            icon = Icons.Default.Policy,
            text = "Chính sách bảo mật",
            onClick = { /* Handle click */ }
        )
        OptionItem(
            icon = Icons.Default.Settings,
            text = "Quy định sử dụng",
            onClick = { /* Handle click */ }
        )
        OptionItem(
            icon = Icons.Default.AccountCircle,
            text = "Đăng xuất",
            onClick = onLogout
        )
    }
}

@Composable
fun OptionItem(icon: ImageVector, text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color(0xFF007BFF), // Blue icon color
            modifier = Modifier.size(24.dp)
        )
      SpacerWidth(16.dp)
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}


