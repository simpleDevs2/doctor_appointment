package com.example.doctorappoint.model

import com.example.doctorappoint.R

data class News(
    val title: String,
    val description: String,
    val imageResId: Int
)

val newsList = listOf(
    News(
        title = "THUẬN TIỆN KHÁM THAI CUỐI TUẦN TẠI BỆNH VIỆN ĐẠI HỌC Y DƯỢC TP. HỒ CHÍ MINH",
        description = "Nhiều mẹ bầu ở xa hay bận rộn trong tuần, lo lắng đi khám không kịp về nhà hoặc chưa hoàn...",
        imageResId = R.drawable.banner
    )
)