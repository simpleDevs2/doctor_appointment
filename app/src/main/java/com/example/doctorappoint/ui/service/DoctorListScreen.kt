package com.example.doctorappoint.ui.theme.service

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.SearchBar
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.model.Doctor
import com.example.doctorappoint.model.dummyDoctorList
import com.example.doctorappoint.ui.theme.SecondaryLight


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    var searchString by remember { mutableStateOf("") }


    val title = "Danh sách bác sĩ"
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)


    ) {
        BackBtnAndTitle(
            modifier,
            title,
            onBackClick = {
                navController.popBackStack()
            }
        )

        SpacerHeight(8.dp)

        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchString = searchString,
            placeholder = "Tìm kiếm bác sĩ",
            onSearchStringChange = { searchString = it }
        )

        SpacerHeight(16.dp)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(dummyDoctorList) { doctor ->
                DoctorCard(doctor = doctor)
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: Doctor, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SecondaryLight)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = doctor.imageResId),
                contentDescription = "Doctor ${doctor.name}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .align(Alignment.Top)
            )
            SpacerWidth(16.dp)
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = doctor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Chuyên trị: ${doctor.specialty}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Lịch khám: ${doctor.schedule}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = "Giá khám: ${doctor.price}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2D8C9D)
                )
                SpacerHeight(8.dp)
                Button(
                    onClick = {  },
                    modifier = Modifier
                        .width(114.dp)
                        .height(32.dp)
                     .align(Alignment.End),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D8C9D))
                ) {
                    Text(text = "Đặt ngay", color = Color.White, fontSize = 14.sp)
                }
            }
        }
    }
}

