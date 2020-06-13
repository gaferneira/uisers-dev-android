package co.tuister.data.dto

import java.io.Serializable

class DataSemesterUserDto(
    val average: String,
    val semesters: MutableList<SemesterUserDto>
) : Serializable {
    constructor() : this("", mutableListOf<SemesterUserDto>())
}
