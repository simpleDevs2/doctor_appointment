package com.example.doctorappoint.model

data class AppointmentResponse(
   val status: Boolean,
    val message: String,
    val data: BookingData
)

data class BookingData(
    val appointment_date: String,
    val appointment_time: String,
    val booking_id: Int,
    val doctor_id: Int,
    val doctor_name: String,
    val room_id: Int,
    val room_name: String,
    val schedule_detail_id: Int,
    val schedule_id: Int,
    val shift: String,
    val working_date: String
)