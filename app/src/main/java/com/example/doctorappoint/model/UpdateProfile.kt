package com.example.doctorappoint.model

data class UpdateProfileResponse(
    val status: Boolean,
    val message: String,
    val data: User
)


data class UpdateProfile(
    val address: String,
    val birthdate: String,
    val gender: String,
    val name: String,
    val phone: String
)