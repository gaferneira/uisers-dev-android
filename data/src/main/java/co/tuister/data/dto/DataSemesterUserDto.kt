package co.tuister.data.dto

import java.io.Serializable

class DataSemesterUserDto(
    var average: Float = 0f,
    var email: String = "",
    var currentSemester: String = ""
) : Serializable
