package com.example.doctorappoint.model

data class ScheduleResponse(
    val message: String?,
    val status: Boolean,
    val data: List<List<ScheduleData>>
)

data class DoctorDailyScheduleResponse(
    val status: Boolean,
    val message: String?,
    val data: DoctorDailySchedule?
)

data class ListDoctorsScheduleResponse(
    val status: Boolean,
    val message: String?,
    val data: List<DoctorInfo>
)

data class DoctorDailySchedule(
    val doctor: DoctorInfo,
    val schedules : List<DailyScheduleDetail>
)

data class ScheduleData(
    val doctors: List<Doctor>,
    val room: Room,
    val shift: String,
    val time: String,
    val working_date: String,
    val department_id: Int,
    val department_name: String,
    val price: Int
)

data class DoctorInfo(
    val id: Int,
    val name: String,
    val image: String?,
    val degree: String,
    val gender: String,
    val department: String,
    val price: Int
)

data class DailyScheduleDetail(
    val schedule_detail_id: Int,
    val working_date: String,
    val shift: String,
    val room: String,
    val slots: List<ScheduleTimeSlot>
)

data class Doctor(
    val schedule_detail_id: Int,
    val id: Int,
    val name: String,
    val image: String?,
    val degree: String,
    val department: Department,
    val slots: List<ScheduleTimeSlot>
)

data class ScheduleTimeSlot(
    val start_time: String,
    val end_time: String,
    val booked_slots_count: Int
)