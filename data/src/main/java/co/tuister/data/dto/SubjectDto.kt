package co.tuister.data.dto

import java.io.Serializable

class SubjectDto(
    val career: String = "",
    val id: Int = 1,
    val name: String = "",
    val credits: Int = 0
) : Serializable

class SubjectContainerDto(
    val id: String = "",
    val subjectDto: SubjectDto
)