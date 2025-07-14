import com.example.doctorappoint.model.Department
import com.example.doctorappoint.model.Room

data class ScheduleResponse(
    val doctors: List<Doctor>,
    val room: Room,
    val shift: String,
    val time: String,
    val working_date: String,
    val department_id: Int,
    val department_name: String,
    val price: Int
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