package co.tuister.domain.entities


data class SubjectClass(
    val subjectName: String,
    val teacher: String,
    val day: Int,
    val initialHour: String,
    val finalHour: String,
    val place: String
)