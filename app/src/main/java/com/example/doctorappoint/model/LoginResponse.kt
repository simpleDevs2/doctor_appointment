package com.example.doctorappoint.model

data class LoginResponse(
    val message: String,
    val status: Boolean,
    val token: String,
    val user: User
)
