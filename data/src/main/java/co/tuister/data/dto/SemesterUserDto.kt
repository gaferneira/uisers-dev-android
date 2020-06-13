package co.tuister.data.dto

import java.io.Serializable

class SemesterUserDto(
    val average: String,
    val period: String,
    val credits: String,
    val subjects: MutableList<SubjectUserDto>
) : Serializable {
    constructor() : this("", "", "", mutableListOf<SubjectUserDto>())
}
