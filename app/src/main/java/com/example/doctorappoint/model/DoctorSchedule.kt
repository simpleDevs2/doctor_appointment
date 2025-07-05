import com.example.doctorappoint.model.Room

data class ScheduleResponse(
    val doctors: List<Doctor>,
    val room: Room,
    val shift: String,
    val time: String,
    val working_date: String,
    val department_id: Int,
    val department_name: String,
    val price: String
)