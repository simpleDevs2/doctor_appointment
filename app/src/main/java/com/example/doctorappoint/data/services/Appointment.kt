package com.example.doctorappoint.data.services

import com.example.doctorappoint.model.AppointmentResponse
import com.example.doctorappoint.model.ConfirmPaymentResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface Appointment{
    @POST("/booking")
    suspend fun makeAppointment(
        @Query("user_id") userId: Int,
        @Query("schedule_detail_id") scheduleDetailId: Int,
        @Query("appointment_time") appointmentTime: String
    ):AppointmentResponse

    @POST("booking/payment-success")
    suspend fun confirmPayment(
        @Query("user_id") userId: Int,
        @Query("booking_id") bookingId: Int,
        @Query("zp_trans_id") zpTransId: String
    ): ConfirmPaymentResponse
}