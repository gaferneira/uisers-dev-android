package co.tuister.data.dto

import java.io.Serializable

class DataSemesterUserDto(
    var average: String,
    var isCurrent: Boolean,
    var email: String,
    val semesters: MutableList<SemesterUserDto>
) : Serializable {
    constructor() : this("", false, "", mutableListOf<SemesterUserDto>())
}
