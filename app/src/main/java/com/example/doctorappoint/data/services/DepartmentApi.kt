package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.Department
import retrofit2.http.GET

interface DepartmentApi {
    @GET("/departmentlist")
    suspend fun getDepartments(): List<Department>
}