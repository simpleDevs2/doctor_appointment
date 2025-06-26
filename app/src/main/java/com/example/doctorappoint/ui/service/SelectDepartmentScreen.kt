package com.example.doctorappoint.ui.theme.service

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.component.BackBtnAndTitle
import com.example.doctorappoint.component.SearchBar
import com.example.doctorappoint.component.SpacerHeight
import com.example.doctorappoint.component.SpacerWidth
import com.example.doctorappoint.model.Department
import com.example.doctorappoint.model.departments

@Composable

fun SelectDepartmentScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier){
    var searchString by remember{ mutableStateOf("") }
    val title = "Chọn chuyên khoa"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 24.dp)



    ){
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
            onSearchStringChange = { searchString = it }
        )
        SpacerHeight(24.dp)
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(departments) { dp ->
                DepartmentCard(department = dp, onClick = {

                })
            }
        }
    }
}

@Composable
fun DepartmentCard(
    department: Department,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(0xFF0066CC)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.noti), // icon chữ i
                    contentDescription = null,
                    tint = Color(0xFF0066CC),
                    modifier = Modifier.size(18.dp)
                )
                SpacerWidth(8.dp)
                Text(
                    text = department.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0066CC),
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = department.price,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                SpacerWidth(4.dp)
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.Black
                )
            }

            department.description?.let {
              SpacerHeight(8.dp)
                Text(
                    text = it,
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.heightIn(max = 60.dp)
                )
            }
        }
    }
}