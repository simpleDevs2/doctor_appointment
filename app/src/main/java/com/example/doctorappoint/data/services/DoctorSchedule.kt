package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.DoctorDailyScheduleResponse
import com.example.doctorappoint.model.ScheduleResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DoctorSchedule {
    @POST("doctorschedule")
    suspend fun getDoctorSchedule(
        @Query("department_id") departmentId: Int,
        @Query("working_date") workingDate: String
    ): Response<ScheduleResponse>

    @GET("doctorFutureSchedules")
    suspend fun getDoctorDailySchedule(
        @Query("doctor_id") doctorId: Int,
    ): Response<DoctorDailyScheduleResponse>

}

