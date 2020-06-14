package co.tuister.data.dto

import co.tuister.domain.entities.Note
import java.io.Serializable

class NoteDto(
    var description: String = "",
    var score: Float = 0f,
    var percentage: Float = 0f,
    var total: Float = 0f
    ) : Serializable

fun NoteDto.toEntity() = Note("", description, score, percentage, total)

fun Note.toDTO() = NoteDto(title, grade, percentage, total
)