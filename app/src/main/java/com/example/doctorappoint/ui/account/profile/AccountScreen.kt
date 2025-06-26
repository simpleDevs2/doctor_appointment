package com.example.doctorappoint.ui.account.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.component.SpacerHeight
import com.example.doctorappoint.component.SpacerWidth

@Composable
fun AccountScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp)

    ) {
        TopSection()
        SpacerHeight(16.dp)
        AccountOptions()
        SpacerHeight(16.dp)
    }
}

@Composable
fun TopSection() {
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
       SpacerHeight(16.dp)
        // Phone Number
        Text(
            text = "094****574",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
fun AccountOptions() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OptionItem(
            icon = Icons.Default.Person,
            text = "Thông tin cá nhân",
            onClick = { /* Handle click */ }
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
            onClick = { /* Handle click */ }
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


