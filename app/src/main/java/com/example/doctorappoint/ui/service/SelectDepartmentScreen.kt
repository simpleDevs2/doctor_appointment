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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.BackBtnAndTitle
import com.example.doctorappoint.common.SearchBar
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.data.api.NetworkResponse
import com.example.doctorappoint.model.Department
import com.example.doctorappoint.ui.service.DepartmentViewModel

@Composable
fun SelectDepartmentScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: DepartmentViewModel = viewModel()
) {
    var searchString by remember { mutableStateOf("") }
    val title = "Chọn chuyên khoa"
    
    val departmentsState by viewModel.departments.collectAsState()

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
            placeholder = "Tìm kiếm chuyên khoa",
            searchString = searchString,
            onSearchStringChange = { searchString = it }
        )
        SpacerHeight(24.dp)
        
        when (departmentsState) {
            is NetworkResponse.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF0066CC))
                    SpacerHeight(16.dp)
                    Text(
                        text = "Đang tải danh sách chuyên khoa...",
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            is NetworkResponse.Success -> {
                val departments = (departmentsState as NetworkResponse.Success<List<Department>>).data
                val filteredDepartments = if (searchString.isNotEmpty()) {
                    departments.filter { it.name.contains(searchString, ignoreCase = true) }
                } else {
                    departments
                }
                
                LazyColumn(
                    modifier = modifier
                        .fillMaxSize()
                        .background(Color.White),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredDepartments) { department ->
                        DepartmentCard(
                            department = department, 
                            onClick = {
                                navController.navigate("doctorList")
                            }
                        )
                    }
                }
            }
            
            is NetworkResponse.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Có lỗi xảy ra khi tải danh sách chuyên khoa",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    SpacerHeight(16.dp)
                    Button(
                        onClick = { viewModel.refreshDepartments() },
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0066CC)
                        )
                    ) {
                        Text("Thử lại")
                    }
                }
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
                    painter = painterResource(id = R.drawable.noti),
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