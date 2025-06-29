package com.example.doctorappoint.model

data class User(
    val address: String,
    val api_token: String,
    val birthdate: String,
    val created_at: String,
    val gender: String,
    val id: Int,
    val name: String,
    val password: String,
    val phone: String,
    val token_expired_at: String,
    val updated_at: String
)