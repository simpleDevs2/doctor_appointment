package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.LoginResponse
import com.example.doctorappoint.model.UpdateProfile
import com.example.doctorappoint.model.UpdateProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface UserApi {

    @FormUrlEncoded
    @POST("/login")
    suspend fun userLogin(
        @Field("phone") phone: String,
        @Field("password") password: String
    ): Response< LoginResponse>

    @POST("/logout")
    suspend fun userLogout(
        @Header("Authorization") token: String
    ): Response<Unit>

    @POST("/update-profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
       @Body updateProfile: UpdateProfile
    ): Response<UpdateProfileResponse>
}