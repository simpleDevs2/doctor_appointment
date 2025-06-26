package com.example.doctorappoint.model

import androidx.annotation.DrawableRes
import com.example.doctorappoint.R

data class Banner(
    val id : Int,
    @DrawableRes val imageResId : Int,
)

val bannerList = listOf(
    Banner(1, R.drawable.banner),
    Banner(2, R.drawable.banner),
)
