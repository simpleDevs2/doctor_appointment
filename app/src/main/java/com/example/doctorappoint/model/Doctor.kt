package com.example.doctorappoint.model

import androidx.annotation.DrawableRes
import com.example.doctorappoint.R

data class Doctor(
    val id: String,
    val name: String,
    val specialty: String,
    val schedule: String,
    val price: String,
    @DrawableRes val imageResId: Int
)

val dummyDoctorList = listOf(
    Doctor(
        id = "doc1",
        name = "BS Nguyễn Đình Hồng Phúc",
        specialty = "Chưa cập nhật",
        schedule = "Thứ 2,3,4,5,6",
        price = "100.000đ",
        imageResId = R.drawable.ic_launcher_background // Assuming you have this image
    ),
    Doctor(
        id = "doc2",
        name = "BS Nguyễn Đình Văn Long",
        specialty = "Nội khoa",
        schedule = "Thứ 2,4,6",
        price = "120.000đ",
        imageResId = R.drawable.ic_launcher_background
    ),
    Doctor(
        id = "doc3",
        name = "BS Lê Thị Kim Anh",
        specialty = "Nhi khoa",
        schedule = "Thứ 3,5,7",
        price = "150.000đ",
        imageResId = R.drawable.ic_launcher_background
    ),
    Doctor(
        id = "doc4",
        name = "BS Trần Minh Khang",
        specialty = "Tim mạch",
        schedule = "Thứ 2,3,4,5,6",
        price = "180.000đ",
        imageResId = R.drawable.ic_launcher_background
    )
)
