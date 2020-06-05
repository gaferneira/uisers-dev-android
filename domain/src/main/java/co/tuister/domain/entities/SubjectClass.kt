package co.tuister.domain.entities


data class SubjectClass(
    val subject: Subject,
    val initialHour: String,
    val finalHour: String,
    val place: String
)