package co.tuister.domain.entities

data class Task(
    val title: String,
    val description: String,
    val initialHour: String?,
    val finalHour: String?
) {
}