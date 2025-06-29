package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.LoginResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserApi {

    @FormUrlEncoded
    @POST("/login")
    suspend fun userLogin(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): LoginResponse
}