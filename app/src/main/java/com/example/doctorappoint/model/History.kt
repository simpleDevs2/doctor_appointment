package com.example.doctorappoint.model

data class HistoryResponse(
    val message: String,
    val status: Boolean,
    val data: List<UserHistory>,
)

data class UserHistory(
    val appointment_date: String,
    val appointment_time: String,
    val department: String,
    val doctor: String,
    val doctorId: Int,
    val payment_amount: String,
    val payment_reference: String,
    val payment_status: String,
    val room: String,
    val schedule_detail_id: Int,
    val status: String
)