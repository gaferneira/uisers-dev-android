package co.tuister.data.dto

import co.tuister.domain.entities.Semester
import java.io.Serializable

class SemesterUserDto(
    val average: String = "",
    val period: String = "",
    val credits: String = ""
) : Serializable

fun SemesterUserDto.toEntity(path: String) = Semester(path, period, average.toFloat(), credits.toInt(), false)

fun Semester.toDTO() = SemesterUserDto(
    average.toString(), period, credits.toString())
