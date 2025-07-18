package com.example.doctorappoint.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.doctorappoint.R
import com.example.doctorappoint.common.LoginManager
import com.example.doctorappoint.common.SearchBar
import com.example.doctorappoint.common.SpacerHeight
import com.example.doctorappoint.common.SpacerWidth
import com.example.doctorappoint.model.News
import com.example.doctorappoint.model.User
import com.example.doctorappoint.model.bannerList
import com.example.doctorappoint.model.newsList
import com.example.doctorappoint.ui.theme.PrimaryColor
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalTime
import network.chaintech.kmp_date_time_picker.utils.now

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    var searchString by remember{ mutableStateOf("") }
    val context = LocalContext.current
    var user by remember { mutableStateOf(LoginManager.getUser(context)) }
    
    LaunchedEffect(Unit) {
        user = LoginManager.getUser(context)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ){
        TopBar(user = user)
        SpacerHeight(12.dp)
        SearchBar(
            modifier = Modifier.fillMaxWidth(),
            searchString = searchString,
            placeholder = "Tìm kiếm dịch vụ",
            onSearchStringChange = { searchString = it }
        )
        SpacerHeight(24.dp)
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                SliderBanner()
                SpacerHeight(16.dp)
                ServicesGrid(navController = navController)
                HighlightNewsSection()
            }
        }
    }
}

@Composable
fun TopBar(modifier: Modifier = Modifier, user: User?){
    val currentHour = LocalTime.now().hour
    val welcomeMessage = when {
        currentHour < 12 -> "Chào buổi sáng"
        currentHour < 17 -> "Chào buổi chiều"
        else -> "Chào buổi tối"
    }
    
    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.avar),
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(1.dp, Color.LightGray, CircleShape)
            )
            SpacerWidth(12.dp)
            Column {
                Text(
                    text = welcomeMessage,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Text(
                    text = user?.name ?: "User",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

            }
        }
        IconButton (onClick = { /* Handle notification click */ }) {
            Icon(painter =  painterResource(id = R.drawable.noti),
                contentDescription = "Lock Icon",
                modifier = Modifier.size(24.dp),
                tint = Color(0xFF9e9e9e)
            )
        }
    }

}

@Composable
fun SliderBanner(){
    val pagerState = rememberPagerState(pageCount =
        { bannerList.size}
    )

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPager = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPager)
        }
    }

    Column (modifier =  Modifier
        .fillMaxWidth()
        .height(180.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Box(modifier = Modifier.wrapContentSize()){
            HorizontalPager(state =  pagerState,
                modifier = Modifier.wrapContentSize()

            ) {
                    currentPage ->
                Card(modifier = Modifier.wrapContentSize(),
                    elevation = CardDefaults.cardElevation(8.dp)
                ){
                    Image(
                        painter = painterResource(id = bannerList[currentPage].imageResId),
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )

                }
            }

        }

    }

}

@Composable
fun ServicesGrid(modifier: Modifier = Modifier,navController: NavHostController) {

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Dịch vụ",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
        )
        SpacerHeight(12.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Hàng 1
            ServiceItem(
                text = "Đặt khám chuyên khoa",
                modifier = Modifier.weight(1f),
                iconResId = R.drawable.stethoscope,
                onclick = {
                     navController.navigate("selectDepartment")
                }
            )
            ServiceItem(
                text = "Đặt khám bác sĩ",
                modifier = Modifier.weight(1f),
                iconResId = R.drawable.doctor,
                onclick = {
                    navController.navigate("list_doctors_schedule")
                }
            )
        }
        SpacerHeight(16.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Hàng 2
            ServiceItem(
                text = "Khám ngoài giờ",
                modifier = Modifier.weight(1f),
                iconResId = R.drawable.calendar,
                onclick = {

                }
            )
            ServiceItem(
                text = "Đặt lịch uống thuốc",
                modifier = Modifier.weight(1f),
                iconResId = R.drawable.pills,
                onclick = {

                }
            )
        }
    }
}

@Composable
fun ServiceItem(
    text: String,
    modifier: Modifier = Modifier,
    iconResId: Int,
    onclick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clickable { onclick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                tint = PrimaryColor,
                modifier = Modifier.size(32.dp)
            )
            SpacerWidth(12.dp)
            Text(
                text = text,
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.W400
            )
        }
    }
}


@Composable
fun HighlightNewsSection(modifier: Modifier = Modifier) {
    Column(modifier = modifier
        .fillMaxWidth()
        .padding(top = 24.dp, bottom = 78.dp)) {
        Text(
            text = "Tin tức nổi bật",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryColor,
        )
        SpacerHeight(12.dp)

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            newsList.forEach { news ->
                NewsCard(news = news)
            }
        }
    }
}

@Composable
fun NewsCard(news: News) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = news.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.Black,
                maxLines = 2
            )
            SpacerHeight(6.dp)

            Text(
                text = news.description,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )
            SpacerHeight(12.dp)

            Image(
                painter = painterResource(id = news.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
    }
}




