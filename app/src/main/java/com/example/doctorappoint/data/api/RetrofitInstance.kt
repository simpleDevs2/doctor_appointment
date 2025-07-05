package com.example.doctorappoint.data.api

import com.example.doctorappoint.data.services.Appointment
import com.example.doctorappoint.data.services.DepartmentApi
import com.example.doctorappoint.data.services.DoctorSchedule
import com.example.doctorappoint.data.services.UserApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val baseUrl = "https://doctorapp.myth.vn"
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    private val userApi: UserApi = retrofit.create(UserApi::class.java)
    private val departmentApi: DepartmentApi = retrofit.create(DepartmentApi::class.java)
    private val doctorSchedule: DoctorSchedule = retrofit.create(DoctorSchedule::class.java)
    private val appointment: Appointment = retrofit.create(Appointment::class.java)

    fun getUserApi(): UserApi = userApi
    fun getDepartmentList(): DepartmentApi = departmentApi
    fun getDoctorSchedule(): DoctorSchedule = doctorSchedule
    fun makeAppointment(): Appointment = appointment

}