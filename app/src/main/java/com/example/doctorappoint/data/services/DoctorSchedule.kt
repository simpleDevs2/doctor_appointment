package com.example.doctorappoint.data.services

import ScheduleResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Query

interface DoctorSchedule {
    @POST("doctorschedule")
    suspend fun getDoctorSchedule(
        @Query("department_id") departmentId: Int,
        @Query("working_date") workingDate: String
    ): Response<List<List<ScheduleResponse>>>

}