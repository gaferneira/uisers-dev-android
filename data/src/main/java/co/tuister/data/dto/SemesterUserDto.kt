package co.tuister.data.dto

import co.tuister.domain.entities.Semester
import java.io.Serializable

class SemesterUserDto(
    val average: String,
    val period: String,
    val credits: String,
    val current: Boolean,
    val subjects: MutableList<SubjectUserDto>
) : Serializable {
    constructor() : this("", "", "", false, mutableListOf<SubjectUserDto>())
}

fun SemesterUserDto.toEntity() = Semester(period, average.toFloat(), credits.toInt(), current)

fun Semester.toDTO() = SemesterUserDto(
    average.toString(), period, credits.toString(), current,
    mutableListOf()
)