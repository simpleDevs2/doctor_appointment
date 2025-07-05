import com.example.doctorappoint.model.Department

data class Doctor(
    val schedule_detail_id: Int,
    val id: Int,
    val name: String,
    val image: String?,
    val degree: String,
    val department: Department
)