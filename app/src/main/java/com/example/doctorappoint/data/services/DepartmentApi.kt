package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.DepartmentItem
import retrofit2.http.GET

interface DepartmentApi {
    @GET("/departmentlist")
    suspend fun getDepartments(): List<DepartmentItem>
}