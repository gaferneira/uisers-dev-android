package co.tuister.data.dto

import java.io.Serializable

class SubjectUserDto(
    val score: String,
    val code: String,
    val teacher: String,
    val credits: String,
    val name: String,
    val notes: MutableList<NoteDto>
) : Serializable {
    constructor() : this("", "", "", "", "", mutableListOf<NoteDto>())
}
