package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.HistoryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface History {
    @GET("booking/history")
    suspend fun getUserHistory(
        @Header("Authorization") token: String,
    ): Response<HistoryResponse>
}
