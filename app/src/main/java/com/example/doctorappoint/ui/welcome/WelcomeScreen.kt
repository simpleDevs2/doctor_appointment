package com.example.doctorappoint.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.doctorappoint.R
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onLoginClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        //Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Thay thế bằng icon của bạn
                contentDescription = "Heart Hands Icon",
                modifier = Modifier.size(48.dp)
            )
            SpacerWidth(16.dp)
            Column {
                Text(
                    text = "BỆNH VIỆN ĐẠI HỌC XYZ HCMC",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50) // Màu xanh đậm/xám
                )
                Text(
                    text = "Thấu hiểu nỗi đau - Niềm tin của bạn",
                    fontSize = 14.sp,
                    color = Color(0xFF6A9B9F) // Màu xanh teal
                )
            }
        }
        //Logo
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f) // Điều chỉnh chiều rộng theo yêu cầu
                    .aspectRatio(16f / 9f) // Giữ tỉ lệ khung hình
                    .background(Color.LightGray, RoundedCornerShape(8.dp)) // Nền tạm thời
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_background), // Thay thế bằng ảnh của bạn
                    contentDescription = "Cute dog",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )


            }

            SpacerHeight(32.dp)
            Text(
                text = "Đặt lịch khám bệnh\ntrực tuyến",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp
            )

            SpacerHeight(8.dp)

            Text(
                text = "Đặt lịch khám bệnh nhanh chóng,\nkhông cần phải xếp hàng\nđợi lấy số",
                fontSize = 14.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 64.dp)
            )
        }
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 64.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D8C9D)) // Màu xanh teal
            ) {
                Text(
                    text = "Đăng nhập",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Button(
                onClick = onRegisterClick ,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF86D0B4)) // Màu xanh lá nhạt
            ) {
                Text(
                    text = "Đăng ký",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
