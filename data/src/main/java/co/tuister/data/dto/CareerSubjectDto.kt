package co.tuister.data.dto

import java.io.Serializable

class CareerSubjectDto(
    val career: String = "",
    val id: Int = 1,
    val name: String = "",
    val credits: Int = 0
) : Serializable
