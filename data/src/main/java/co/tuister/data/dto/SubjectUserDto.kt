package co.tuister.data.dto

import co.tuister.domain.entities.Subject
import java.io.Serializable

class SubjectUserDto(
    val code: String = "",
    val name: String = "",
    var teacher: String = "",
    var note: Float = 0f,
    val credits: Int = 0,
    var materialColor: Int = 0,
    val path: String = ""
) : Serializable

fun SubjectUserDto.toEntity(path: String) = Subject(path, code, name, teacher, note, credits, materialColor)

fun Subject.toDTO() = SubjectUserDto(
    code, name, teacher, note, credits, materialColor, id
)
